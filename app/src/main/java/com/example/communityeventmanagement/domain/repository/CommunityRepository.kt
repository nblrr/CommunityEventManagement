package com.example.communityeventmanagement.domain.repository

import com.example.communityeventmanagement.domain.entities.Community
import com.example.communityeventmanagement.domain.entities.Event
import com.example.communityeventmanagement.domain.entities.ForumMessage
import com.example.communityeventmanagement.domain.entities.User
import com.example.communityeventmanagement.domain.util.Resource
import kotlinx.coroutines.flow.StateFlow

interface CommunityRepository {
    val communities: StateFlow<List<Community>>
    val joinedCommunityIds: StateFlow<Set<Int>>
    val registeredEventIds: StateFlow<Set<Int>>

    suspend fun loadCommunities(users: List<User> = emptyList())
    suspend fun saveForumData(communityId: Int)
    suspend fun refreshUserParticipation(currentUser: User?)
    suspend fun toggleCommunityJoin(communityId: Int, userId: String): Resource<Unit>
    suspend fun toggleEventRegistration(communityId: Int, eventId: Int, userId: String): Resource<Unit>
    suspend fun addEventRating(communityId: Int, eventId: Int, userId: String, userName: String, score: Int, comment: String): Resource<Unit>
    suspend fun addGalleryImage(communityId: Int, eventId: Int, imageUri: String): Resource<Unit>
    suspend fun addForumMessage(communityId: Int, message: ForumMessage): Resource<Unit>
    suspend fun addEvent(communityId: Int, event: Event): Resource<Unit>
    suspend fun updateEvent(communityId: Int, event: Event): Resource<Unit>
    suspend fun deleteEvent(communityId: Int, eventId: Int): Resource<Unit>
    suspend fun addCommunity(community: Community): Resource<Unit>
    suspend fun updateCommunity(community: Community): Resource<Unit>
    suspend fun deleteCommunity(communityId: Int): Resource<Unit>
    suspend fun saveCommunities(): Resource<Unit>
    suspend fun getEvent(eventId: Int, communityId: Int): Event?
    fun getRecommendedCommunities(): List<Community>
    fun getRecommendedEvents(isUpcoming: (String) -> Boolean): List<Event>
}
