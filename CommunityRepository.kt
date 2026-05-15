package com.example.communityeventmanagement.data.repository

import androidx.compose.runtime.mutableStateListOf
import com.example.communityeventmanagement.data.local.JsonStorage
import com.example.communityeventmanagement.data.model.Community
import com.example.communityeventmanagement.data.model.Event
import com.example.communityeventmanagement.data.model.Rating
import com.example.communityeventmanagement.data.model.UserProfile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommunityRepository(private val storage: JsonStorage) {
    val communities = mutableStateListOf<Community>()
    val joinedCommunityIds = mutableStateListOf<Int>()
    val registeredEventIds = mutableStateListOf<Int>()

    // Load communities and their forum messages
    suspend fun loadCommunities() {
        val loadedCommunities = storage.loadCommunities().map { community ->
            @Suppress("SENSELESS_COMPARISON")
            val sanitizedEvents = if (community.events == null) emptyList() else community.events.map { event ->
                if (event.registeredUserIds == null || event.galleryImages == null || event.ratings == null || event.time == null) {
                    event.copy(
                        registeredUserIds = event.registeredUserIds ?: emptyList(),
                        galleryImages = event.galleryImages ?: emptyList(),
                        ratings = event.ratings ?: emptyList(),
                        time = event.time ?: ""
                    )
                } else event
            }
            
            @Suppress("SENSELESS_COMPARISON")
            if (community.memberIds == null || community.events == null || community.forumMessages == null) {
                community.copy(
                    memberIds = community.memberIds ?: emptyList(),
                    events = sanitizedEvents,
                    forumMessages = community.forumMessages ?: emptyList()
                )
            } else {
                community.copy(events = sanitizedEvents)
            }
        }
        communities.clear()
        communities.addAll(loadedCommunities)
        communities.forEachIndexed { index, community ->
            val messages = storage.loadForumMessages(community.id)
            if (messages.isNotEmpty()) {
                communities[index] = community.copy(forumMessages = messages)
            }
        }
    }

    suspend fun saveCommunityData() = storage.saveCommunities(communities.toList())
    suspend fun saveForumData(communityId: Int) {
        communities.find { it.id == communityId }?.let { 
            storage.saveForumMessages(communityId, it.forumMessages) 
        }
    }

    fun refreshUserParticipation(currentUser: UserProfile?) {
        joinedCommunityIds.clear()
        registeredEventIds.clear()
        val userId = currentUser?.id ?: return
        communities.forEach { community ->
            if (community.organizerId == userId || community.memberIds.contains(userId)) {
                if (!joinedCommunityIds.contains(community.id)) joinedCommunityIds.add(community.id)
            }
            community.events.forEach { event ->
                if (event.registeredUserIds.contains(userId)) {
                    if (!registeredEventIds.contains(event.id)) registeredEventIds.add(event.id)
                    if (!joinedCommunityIds.contains(community.id)) joinedCommunityIds.add(community.id)
                }
            }
        }
    }

    suspend fun toggleCommunityJoin(communityId: Int, userId: String) {
        val index = communities.indexOfFirst { it.id == communityId }
        if (index != -1) {
            val community = communities[index]
            val isJoined = community.memberIds.contains(userId)
            val newMemberIds = if (isJoined) {
                joinedCommunityIds.removeAll { it == communityId }
                community.memberIds - userId
            } else {
                if (!joinedCommunityIds.contains(communityId)) joinedCommunityIds.add(communityId)
                community.memberIds + userId
            }
            communities[index] = community.copy(memberIds = newMemberIds)
            saveCommunityData()
        }
    }

    suspend fun toggleEventRegistration(communityId: Int, eventId: Int, userId: String) {
        val communityIndex = communities.indexOfFirst { it.id == communityId }
        if (communityIndex != -1) {
            val community = communities[communityIndex]
            val eventIndex = community.events.indexOfFirst { it.id == eventId }
            if (eventIndex != -1) {
                val event = community.events[eventIndex]
                val isRegistered = event.registeredUserIds.contains(userId)
                val newRegisteredIds = if (isRegistered) {
                    registeredEventIds.removeAll { it == eventId }
                    event.registeredUserIds - userId
                } else {
                    if (!registeredEventIds.contains(eventId)) registeredEventIds.add(eventId)
                    event.registeredUserIds + userId
                }
                val updatedEvents = community.events.toMutableList()
                updatedEvents[eventIndex] = event.copy(registeredUserIds = newRegisteredIds)
                var newMemberIds = community.memberIds
                if (!isRegistered && !community.memberIds.contains(userId)) {
                    newMemberIds = community.memberIds + userId
                    if (!joinedCommunityIds.contains(communityId)) joinedCommunityIds.add(communityId)
                }
                communities[communityIndex] = community.copy(events = updatedEvents, memberIds = newMemberIds)
                saveCommunityData()
            }
        }
    }

    suspend fun addEventRating(communityId: Int, eventId: Int, userId: String, userName: String, score: Int, comment: String) {
        val communityIndex = communities.indexOfFirst { it.id == communityId }
        if (communityIndex != -1) {
            val community = communities[communityIndex]
            val eventIndex = community.events.indexOfFirst { it.id == eventId }
            if (eventIndex != -1) {
                val event = community.events[eventIndex]
                if (event.ratings?.any { it.userId == userId } == true) return
                val newRating = Rating(
                    userId = userId, 
                    userName = userName, 
                    score = score.coerceIn(1, 5), 
                    comment = comment.trim(), 
                    date = SimpleDateFormat("d M yyyy", Locale.getDefault()).format(Date())
                )
                val updatedEvents = community.events.toMutableList()
                updatedEvents[eventIndex] = event.copy(ratings = (event.ratings ?: emptyList()) + newRating)
                communities[communityIndex] = community.copy(events = updatedEvents)
                saveCommunityData()
            }
        }
    }

    suspend fun addGalleryImage(communityId: Int, eventId: Int, imageUri: String) {
        val communityIndex = communities.indexOfFirst { it.id == communityId }
        if (communityIndex != -1) {
            val community = communities[communityIndex]
            val eventIndex = community.events.indexOfFirst { it.id == eventId }
            if (eventIndex != -1) {
                val event = community.events[eventIndex]
                val updatedEvents = community.events.toMutableList()
                updatedEvents[eventIndex] = event.copy(galleryImages = (event.galleryImages ?: emptyList()) + imageUri)
                communities[communityIndex] = community.copy(events = updatedEvents)
                saveCommunityData()
            }
        }
    }

    fun getRecommendedCommunities(): List<Community> {
        if (joinedCommunityIds.isEmpty() && registeredEventIds.isEmpty()) {
            return communities.sortedByDescending { it.memberCount }.take(10)
        }
        val userCategories = communities.filter { it.id in joinedCommunityIds }.map { it.category }.toSet()
        return communities.filter { it.id !in joinedCommunityIds }
            .sortedByDescending { if (it.category in userCategories) 10 else 0 }
            .take(10)
    }

    fun getRecommendedEvents(isUpcoming: (String) -> Boolean): List<Event> {
        val allEvents = communities.flatMap { it.events }.filter { isUpcoming(it.date) }
        if (joinedCommunityIds.isEmpty() && registeredEventIds.isEmpty()) {
            return allEvents.sortedByDescending { it.attendeeCount }.take(10)
        }
        val userCategories = communities.filter { it.id in joinedCommunityIds }.map { it.category }.toSet()
        return allEvents.filter { it.id !in registeredEventIds }
            .sortedByDescending { if (it.category in userCategories) 10 else 0 }
            .take(10)
    }
}
