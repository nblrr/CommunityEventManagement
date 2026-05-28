package com.example.communityeventmanagement.data.repository

import android.util.Log
import com.example.communityeventmanagement.data.mapper.toDomain
import com.example.communityeventmanagement.data.mapper.toDto
import com.example.communityeventmanagement.data.source.local.JsonDataSource
import com.example.communityeventmanagement.domain.entities.Community
import com.example.communityeventmanagement.domain.entities.Event
import com.example.communityeventmanagement.domain.entities.ForumMessage
import com.example.communityeventmanagement.domain.entities.Rating
import com.example.communityeventmanagement.domain.entities.User
import com.example.communityeventmanagement.domain.repository.CommunityRepository
import com.example.communityeventmanagement.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommunityRepositoryImpl @Inject constructor(private val dataSource: JsonDataSource) : CommunityRepository {
    private val tag = "CommunityRepositoryImpl"

    private val _communities = MutableStateFlow<List<Community>>(emptyList())
    override val communities: StateFlow<List<Community>> = _communities.asStateFlow()

    private val _joinedCommunityIds = MutableStateFlow<Set<Int>>(emptySet())
    override val joinedCommunityIds: StateFlow<Set<Int>> = _joinedCommunityIds.asStateFlow()

    private val _registeredEventIds = MutableStateFlow<Set<Int>>(emptySet())
    override val registeredEventIds: StateFlow<Set<Int>> = _registeredEventIds.asStateFlow()

    override suspend fun loadCommunities(users: List<User>) {
        Log.d(tag, "Loading communities")
        val loadedCommunityDtos = dataSource.loadList<com.example.communityeventmanagement.data.dto.CommunityDto>("communities.json")
        val allForumMessages = dataSource.loadList<com.example.communityeventmanagement.data.dto.ForumMessageDto>("forum_messages.json").map { it.toDomain() }
        
        val loadedCommunities = loadedCommunityDtos.map { dto ->
            var community = dto.toDomain()
            
            if (users.isNotEmpty()) {
                users.find { it.id == community.organizerId }?.let { user ->
                    community = community.copy(organizerName = user.name)
                }
            }

            val filteredMessages = allForumMessages.filter { it.communityId == community.id }
            if (filteredMessages.isNotEmpty()) {
                community = community.copy(forumMessages = filteredMessages)
            }
            community
        }
        _communities.value = loadedCommunities
    }

    private suspend fun persistCommunities() {
        dataSource.saveCommunities(_communities.value.map { it.toDto() })
    }

    override suspend fun saveForumData(communityId: Int) {
        val allCurrentMessages = _communities.value.flatMap { it.forumMessages }.map { it.toDto() }
        dataSource.saveAllForumMessages(allCurrentMessages)
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

    override suspend fun toggleCommunityJoin(communityId: Int, userId: String): Resource<Unit> = safeCall {
        val community = _communities.value.find { it.id == communityId } ?: throw Exception("Community not found")
        val isJoined = community.memberIds.contains(userId)
        updateCommunityMembers(communityId, userId, !isJoined)
        persistCommunities()
        refreshUserParticipation(User(userId, "", ""))
    }

    override suspend fun toggleEventRegistration(communityId: Int, eventId: Int, userId: String): Resource<Unit> = safeCall {
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

    override suspend fun addEventRating(communityId: Int, eventId: Int, userId: String, userName: String, score: Int, comment: String): Resource<Unit> = safeCall {
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

    override suspend fun addGalleryImage(communityId: Int, eventId: Int, imageUri: String): Resource<Unit> = safeCall {
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

    override suspend fun addCommunity(community: Community): Resource<Unit> = safeCall {
        _communities.value = _communities.value + community
        persistCommunities()
    }

    override suspend fun updateCommunity(community: Community): Resource<Unit> = safeCall {
        _communities.value = _communities.value.map {
            if (it.id == community.id) community else it
        }
        persistCommunities()
    }

    override suspend fun deleteCommunity(communityId: Int): Resource<Unit> = safeCall {
        _communities.value = _communities.value.filter { it.id != communityId }
        persistCommunities()
    }

    override suspend fun addEvent(communityId: Int, event: Event): Resource<Unit> = safeCall {
        val updatedCommunities = _communities.value.map { community ->
            if (community.id == communityId) {
                community.copy(events = community.events + event)
            } else community
        }
        _communities.value = updatedCommunities
        persistCommunities()
    }

    override suspend fun updateEvent(communityId: Int, event: Event): Resource<Unit> = safeCall {
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
    }

    override suspend fun deleteEvent(communityId: Int, eventId: Int): Resource<Unit> = safeCall {
        val updatedCommunities = _communities.value.map { community ->
            if (community.id == communityId) {
                val updatedEvents = community.events.filter { it.id != eventId }
                community.copy(events = updatedEvents)
            } else community
        }
        _communities.value = updatedCommunities
        persistCommunities()
    }

    override suspend fun addForumMessage(communityId: Int, message: ForumMessage): Resource<Unit> = safeCall {
        val updatedCommunities = _communities.value.map { community ->
            if (community.id == communityId) {
                community.copy(forumMessages = community.forumMessages + message)
            } else community
        }
        _communities.value = updatedCommunities
        saveForumData(communityId)
    }

    override suspend fun saveCommunities(): Resource<Unit> = safeCall {
        persistCommunities()
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
