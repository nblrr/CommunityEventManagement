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

class CommunityRepository(private val storage: JsonStorage?) {
    val communities = mutableStateListOf<Community>()
    val joinedCommunityIds = mutableStateListOf<Int>()
    val registeredEventIds = mutableStateListOf<Int>()

    // Load data komunitas
    fun loadCommunities() {
        storage?.let {
            val loadedCommunities = it.loadCommunities()
            communities.clear(); communities.addAll(loadedCommunities)
            communities.forEachIndexed { index, community ->
                val messages = it.loadForumMessages(community.id)
                if (messages.isNotEmpty()) communities[index] = community.copy(forumMessages = messages)
            }
        }
    }

    fun saveCommunityData() = storage?.saveCommunities(communities.toList())
    fun saveForumData(communityId: Int) {
        communities.find { it.id == communityId }?.let { storage?.saveForumMessages(communityId, it.forumMessages) }
    }

    fun refreshUserParticipation(currentUser: UserProfile?) {
        joinedCommunityIds.clear()
        registeredEventIds.clear()
        val userId = currentUser?.id ?: return
        communities.forEach { community ->
            if (community.organizerId == userId || community.memberIds.contains(userId)) {
                joinedCommunityIds.add(community.id)
            }
            community.events.forEach { event ->
                if (event.registeredUserIds.contains(userId)) {
                    registeredEventIds.add(event.id)
                    if (!joinedCommunityIds.contains(community.id)) joinedCommunityIds.add(community.id)
                }
            }
        }
    }

    fun toggleCommunityJoin(communityId: Int, userId: String) {
        val index = communities.indexOfFirst { it.id == communityId }
        if (index != -1) {
            val community = communities[index]
            val isJoined = community.memberIds.contains(userId)
            val newMemberIds = if (isJoined) {
                joinedCommunityIds.remove(communityId)
                community.memberIds - userId
            } else {
                joinedCommunityIds.add(communityId)
                community.memberIds + userId
            }
            communities[index] = community.copy(memberIds = newMemberIds)
            saveCommunityData()
        }
    }

    fun toggleEventRegistration(communityId: Int, eventId: Int, userId: String) {
        val commIndex = communities.indexOfFirst { it.id == communityId }
        if (commIndex != -1) {
            val community = communities[commIndex]
            val eventIndex = community.events.indexOfFirst { it.id == eventId }
            if (eventIndex != -1) {
                val event = community.events[eventIndex]
                val isRegistered = event.registeredUserIds.contains(userId)
                val newRegisteredIds = if (isRegistered) {
                    registeredEventIds.remove(eventId)
                    event.registeredUserIds - userId
                } else {
                    registeredEventIds.add(eventId)
                    event.registeredUserIds + userId
                }
                val updatedEvents = community.events.toMutableList()
                updatedEvents[eventIndex] = event.copy(registeredUserIds = newRegisteredIds)
                var newMemberIds = community.memberIds
                if (!isRegistered && !community.memberIds.contains(userId)) {
                    newMemberIds = community.memberIds + userId
                    if (!joinedCommunityIds.contains(communityId)) joinedCommunityIds.add(communityId)
                }
                communities[commIndex] = community.copy(events = updatedEvents, memberIds = newMemberIds)
                saveCommunityData()
            }
        }
    }

    fun addEventRating(communityId: Int, eventId: Int, userId: String, userName: String, score: Int, comment: String) {
        val communityIndex = communities.indexOfFirst { it.id == communityId }
        if (communityIndex != -1) {
            val community = communities[communityIndex]
            val eventIndex = community.events.indexOfFirst { it.id == eventId }
            if (eventIndex != -1) {
                val event = community.events[eventIndex]
                if (event.ratings?.any { it.userId == userId } == true) return
                val newRating = Rating(userId, userName, score.coerceIn(1, 5), comment.trim(), SimpleDateFormat("d M yyyy", Locale.getDefault()).format(Date()))
                val updatedEvents = community.events.toMutableList()
                updatedEvents[eventIndex] = event.copy(ratings = (event.ratings ?: emptyList()) + newRating)
                communities[communityIndex] = community.copy(events = updatedEvents)
                saveCommunityData()
            }
        }
    }

    fun addGalleryImage(communityId: Int, eventId: Int, imageUri: String) {
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
        if (joinedCommunityIds.isEmpty() && registeredEventIds.isEmpty()) return communities.sortedByDescending { it.memberCount }.take(10)
        val userCategories = communities.filter { it.id in joinedCommunityIds }.map { it.category }.toSet()
        return communities.filter { it.id !in joinedCommunityIds }.sortedByDescending { if (it.category in userCategories) 10 else 0 }.take(10)
    }

    fun getRecommendedEvents(isUpcoming: (String) -> Boolean): List<Event> {
        val allEvents = communities.flatMap { it.events }.filter { isUpcoming(it.date) }
        if (joinedCommunityIds.isEmpty() && registeredEventIds.isEmpty()) return allEvents.sortedByDescending { it.attendeeCount }.take(10)
        val userCategories = communities.filter { it.id in joinedCommunityIds }.map { it.category }.toSet()
        return allEvents.filter { it.id !in registeredEventIds }.sortedByDescending { if (it.category in userCategories) 10 else 0 }.take(10)
    }
}
