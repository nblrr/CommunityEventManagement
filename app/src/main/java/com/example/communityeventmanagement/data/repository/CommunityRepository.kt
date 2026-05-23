package com.example.communityeventmanagement.data.repository

import com.example.communityeventmanagement.data.mapper.toDomain
import com.example.communityeventmanagement.data.mapper.toDto
import com.example.communityeventmanagement.data.source.local.JsonDataSource
import com.example.communityeventmanagement.domain.entities.Community
import com.example.communityeventmanagement.domain.entities.Event
import com.example.communityeventmanagement.domain.entities.ForumMessage
import com.example.communityeventmanagement.domain.entities.Rating
import com.example.communityeventmanagement.domain.entities.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.communityeventmanagement.domain.repository.CommunityRepository as ICommunityRepository

class CommunityRepository(private val dataSource: JsonDataSource) : ICommunityRepository {
    private val _communities = MutableStateFlow<List<Community>>(emptyList())
    override val communities: StateFlow<List<Community>> = _communities.asStateFlow()

    private val _joinedCommunityIds = MutableStateFlow<Set<Int>>(emptySet())
    override val joinedCommunityIds: StateFlow<Set<Int>> = _joinedCommunityIds.asStateFlow()

    private val _registeredEventIds = MutableStateFlow<Set<Int>>(emptySet())
    override val registeredEventIds: StateFlow<Set<Int>> = _registeredEventIds.asStateFlow()

    override suspend fun loadCommunities(users: List<User>) {
        val loadedCommunityDtos = dataSource.loadCommunities()
        val loadedCommunities = loadedCommunityDtos.map { dto ->
            // Use Mapper but force-resolve memberIds and name from Master Data
            val communityFromDto = dto.toDomain()
            
            // 1. Force Member IDs from DTO (Ensure no loss in SSOT)
            val newMemberIds = dto.memberIds ?: emptyList()
            var community = communityFromDto.copy(
                memberIds = newMemberIds,
                memberCount = if (dto.memberCount != null) dto.memberCount else newMemberIds.size
            )

            // 2. Resolve organizer name from master user list IF FOUND
            // This prevents overwriting with "Unknown" if user is not in the list
            if (users.isNotEmpty()) {
                users.find { it.id == community.organizerId }?.let { user ->
                    community = community.copy(organizerName = user.name)
                }
            }

            // 3. Load forum messages separately
            val messages = dataSource.loadForumMessages(community.id).map { it.toDomain() }
            if (messages.isNotEmpty()) {
                community.copy(forumMessages = messages)
            } else {
                community
            }
        }
        _communities.value = loadedCommunities
    }

    private suspend fun persistCommunities(): Result<Unit> {
        return try {
            dataSource.saveCommunities(_communities.value.map { it.toDto() })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveForumData(communityId: Int) {
        try {
            _communities.value.find { it.id == communityId }?.let { community ->
                dataSource.saveForumMessages(communityId, community.forumMessages.map { it.toDto() })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun refreshUserParticipation(currentUser: User?) {
        val userId = currentUser?.id
        if (userId == null) {
            _joinedCommunityIds.value = emptySet()
            _registeredEventIds.value = emptySet()
            return
        }

        val joinedIds = mutableSetOf<Int>()
        val registeredIds = mutableSetOf<Int>()

        _communities.value.forEach { community ->
            if (community.organizerId == userId || community.memberIds.contains(userId)) {
                joinedIds.add(community.id)
            }
            community.events.forEach { event ->
                if (event.registeredUserIds.contains(userId)) {
                    registeredIds.add(event.id)
                    joinedIds.add(community.id)
                }
            }
        }
        _joinedCommunityIds.value = joinedIds
        _registeredEventIds.value = registeredIds
    }

    private fun updateCommunityMembers(communityId: Int, userId: String, isJoining: Boolean) {
        val updatedCommunities = _communities.value.map { community ->
            if (community.id == communityId) {
                val alreadyJoined = community.memberIds.contains(userId)
                if (isJoining && !alreadyJoined) {
                    val newMemberIds = community.memberIds + userId
                    community.copy(memberIds = newMemberIds, memberCount = newMemberIds.size)
                } else if (!isJoining && alreadyJoined) {
                    // When leaving community, also cancel all event registrations in this community
                    val updatedEvents = community.events.map { event ->
                        if (event.registeredUserIds.contains(userId)) {
                            val newRegisteredIds = event.registeredUserIds - userId
                            event.copy(registeredUserIds = newRegisteredIds, attendeeCount = newRegisteredIds.size)
                        } else event
                    }
                    val newMemberIds = community.memberIds - userId
                    community.copy(
                        memberIds = newMemberIds,
                        memberCount = newMemberIds.size,
                        events = updatedEvents
                    )
                } else community
            } else community
        }
        _communities.value = updatedCommunities
    }

    override suspend fun toggleCommunityJoin(communityId: Int, userId: String) {
        val community = _communities.value.find { it.id == communityId } ?: return
        val isJoined = community.memberIds.contains(userId)
        updateCommunityMembers(communityId, userId, !isJoined)
        persistCommunities()
        refreshUserParticipation(User(userId, "", ""))
    }

    override suspend fun toggleEventRegistration(communityId: Int, eventId: Int, userId: String) {
        val updatedCommunities = _communities.value.map { community ->
            if (community.id == communityId) {
                val updatedEvents = community.events.map { event ->
                    if (event.id == eventId) {
                        val isRegistered = event.registeredUserIds.contains(userId)
                        if (!isRegistered && event.maxAttendees > 0 && event.registeredUserIds.size >= event.maxAttendees) {
                            event
                        } else {
                            val newRegisteredIds = if (isRegistered) {
                                event.registeredUserIds - userId
                            } else {
                                event.registeredUserIds + userId
                            }
                            event.copy(registeredUserIds = newRegisteredIds, attendeeCount = newRegisteredIds.size)
                        }
                    } else event
                }
                community.copy(events = updatedEvents)
            } else community
        }
        _communities.value = updatedCommunities

        val community = _communities.value.find { it.id == communityId }
        val event = community?.events?.find { it.id == eventId }
        val isNowRegistered = event?.registeredUserIds?.contains(userId) == true
        
        updateCommunityMembers(communityId, userId, isNowRegistered)
        persistCommunities()
        refreshUserParticipation(User(userId, "", ""))
    }

    override suspend fun addEventRating(communityId: Int, eventId: Int, userId: String, userName: String, score: Int, comment: String) {
        val updatedCommunities = _communities.value.map { community ->
            if (community.id == communityId) {
                val updatedEvents = community.events.map { event ->
                    if (event.id == eventId && !event.ratings.any { it.userId == userId }) {
                        val newRating = Rating(
                            userId = userId,
                            userName = userName,
                            score = score.coerceIn(1, 5),
                            comment = comment.trim(),
                            date = SimpleDateFormat("d M yyyy", Locale.getDefault()).format(Date())
                        )
                        event.copy(ratings = event.ratings + newRating)
                    } else event
                }
                community.copy(events = updatedEvents)
            } else community
        }
        _communities.value = updatedCommunities
        persistCommunities()
    }

    override suspend fun addGalleryImage(communityId: Int, eventId: Int, imageUri: String) {
        val updatedCommunities = _communities.value.map { community ->
            if (community.id == communityId) {
                val updatedEvents = community.events.map { event ->
                    if (event.id == eventId) {
                        event.copy(galleryImages = event.galleryImages + imageUri)
                    } else event
                }
                community.copy(events = updatedEvents)
            } else community
        }
        _communities.value = updatedCommunities
        persistCommunities()
    }

    override suspend fun addCommunity(community: Community): Result<Unit> {
        return try {
            _communities.value = _communities.value + community
            persistCommunities()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCommunity(community: Community): Result<Unit> {
        return try {
            _communities.value = _communities.value.map {
                if (it.id == community.id) community else it
            }
            persistCommunities()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCommunity(communityId: Int): Result<Unit> {
        return try {
            _communities.value = _communities.value.filter { it.id != communityId }
            persistCommunities()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addEvent(communityId: Int, event: Event): Result<Unit> {
        return try {
            val updatedCommunities = _communities.value.map { community ->
                if (community.id == communityId) {
                    community.copy(events = community.events + event)
                } else community
            }
            _communities.value = updatedCommunities
            persistCommunities()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateEvent(communityId: Int, event: Event): Result<Unit> {
        return try {
            val updatedCommunities = _communities.value.map { community ->
                if (community.id == communityId) {
                    val updatedEvents = community.events.map { 
                        if (it.id == event.id) event else it
                    }
                    community.copy(events = updatedEvents)
                } else community
            }
            _communities.value = updatedCommunities
            persistCommunities()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteEvent(communityId: Int, eventId: Int): Result<Unit> {
        return try {
            val updatedCommunities = _communities.value.map { community ->
                if (community.id == communityId) {
                    val updatedEvents = community.events.filter { it.id != eventId }
                    community.copy(events = updatedEvents)
                } else community
            }
            _communities.value = updatedCommunities
            persistCommunities()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addForumMessage(communityId: Int, message: ForumMessage): Result<Unit> {
        return try {
            val updatedCommunities = _communities.value.map { community ->
                if (community.id == communityId) {
                    community.copy(forumMessages = community.forumMessages + message)
                } else community
            }
            _communities.value = updatedCommunities
            saveForumData(communityId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveCommunities(): Result<Unit> {
        return try {
            persistCommunities()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getEvent(eventId: Int, communityId: Int): Event? {
        return _communities.value.find { it.id == communityId }?.events?.find { it.id == eventId }
    }

    override fun getRecommendedCommunities(): List<Community> {
        val joinedIds = _joinedCommunityIds.value
        if (joinedIds.isEmpty() && _registeredEventIds.value.isEmpty()) {
            return _communities.value.sortedByDescending { it.memberCount }.take(10)
        }
        val userCategories = _communities.value.filter { it.id in joinedIds }.map { it.category }.toSet()
        return _communities.value.filter { it.id !in joinedIds }
            .sortedByDescending { if (it.category in userCategories) 10 else 0 }
            .take(10)
    }

    override fun getRecommendedEvents(isUpcoming: (String) -> Boolean): List<Event> {
        val registeredIds = _registeredEventIds.value
        val allEvents = _communities.value.flatMap { it.events }.filter { isUpcoming(it.date) }
        if (_joinedCommunityIds.value.isEmpty() && registeredIds.isEmpty()) {
            return allEvents.sortedByDescending { it.attendeeCount }.take(10)
        }
        val userCategories = _communities.value.filter { it.id in _joinedCommunityIds.value }.map { it.category }.toSet()
        return allEvents.filter { it.id !in registeredIds }
            .sortedByDescending { if (it.category in userCategories) 10 else 0 }
            .take(10)
    }
}
