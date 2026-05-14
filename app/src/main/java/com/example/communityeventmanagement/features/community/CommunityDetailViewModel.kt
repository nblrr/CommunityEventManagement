package com.example.communityeventmanagement.features.community

import androidx.lifecycle.ViewModel
import com.example.communityeventmanagement.data.repository.CommunityRepository
import com.example.communityeventmanagement.data.repository.UserRepository

class CommunityDetailViewModel(
    private val userRepository: UserRepository,
    private val communityRepository: CommunityRepository
) : ViewModel() {
    fun getCommunity(id: Int) = communityRepository.communities.find { it.id == id }
    
    fun toggleJoin(communityId: Int) {
        val userId = userRepository.currentUser?.id ?: return
        communityRepository.toggleCommunityJoin(communityId, userId)
    }
    
    fun isJoined(communityId: Int) = communityRepository.joinedCommunityIds.contains(communityId)

    fun isEventRegistered(eventId: Int) = communityRepository.registeredEventIds.contains(eventId)
}
