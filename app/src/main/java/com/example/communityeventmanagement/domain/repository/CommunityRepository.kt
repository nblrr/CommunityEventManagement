package com.example.communityeventmanagement.domain.repository

import com.example.communityeventmanagement.domain.entities.Community
import com.example.communityeventmanagement.domain.entities.Event
import com.example.communityeventmanagement.domain.entities.ForumMessage
import com.example.communityeventmanagement.domain.entities.User
import kotlinx.coroutines.flow.StateFlow

interface CommunityRepository {
    val communities: StateFlow<List<Community>>
    val joinedCommunityIds: StateFlow<Set<Int>>
    val registeredEventIds: StateFlow<Set<Int>>

    suspend fun loadCommunities(users: List<User> = emptyList())
    suspend fun saveForumData(communityId: Int)
    suspend fun refreshUserParticipation(currentUser: User?)
    suspend fun toggleCommunityJoin(communityId: Int, userId: String)
    suspend fun toggleEventRegistration(communityId: Int, eventId: Int, userId: String)
    suspend fun addEventRating(communityId: Int, eventId: Int, userId: String, userName: String, score: Int, comment: String)
    suspend fun addGalleryImage(communityId: Int, eventId: Int, imageUri: String)
    suspend fun addForumMessage(communityId: Int, message: ForumMessage): Result<Unit>
    suspend fun addEvent(communityId: Int, event: Event): Result<Unit>
    suspend fun updateEvent(communityId: Int, event: Event): Result<Unit>
    suspend fun deleteEvent(communityId: Int, eventId: Int): Result<Unit>
    suspend fun addCommunity(community: Community): Result<Unit>
    suspend fun updateCommunity(community: Community): Result<Unit>
    suspend fun deleteCommunity(communityId: Int): Result<Unit>
    suspend fun saveCommunities(): Result<Unit>
    suspend fun getEvent(eventId: Int, communityId: Int): Event?
    fun getRecommendedCommunities(): List<Community>
    fun getRecommendedEvents(isUpcoming: (String) -> Boolean): List<Event>
}
