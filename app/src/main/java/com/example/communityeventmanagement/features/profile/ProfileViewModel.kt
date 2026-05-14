package com.example.communityeventmanagement.features.profile

import androidx.lifecycle.ViewModel
import com.example.communityeventmanagement.data.repository.CommunityRepository
import com.example.communityeventmanagement.data.repository.UserRepository

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val communityRepository: CommunityRepository,
) : ViewModel() {
    val joinedCommunities
        get() = communityRepository.communities.filter { it.id in communityRepository.joinedCommunityIds }
        
    val managedCommunities
        get() = communityRepository.communities.filter { it.organizerId == userRepository.currentUser?.id }

    fun submitTrustedApplication(reason: String, experience: String) {
        userRepository.submitTrustedApplication(communityRepository.communities, reason, experience)
    }

    fun updateAvatar(newUri: String?) {
        userRepository.updateAvatar(newUri)
    }

    fun logout() {
        userRepository.logout()
        communityRepository.joinedCommunityIds.clear()
        communityRepository.registeredEventIds.clear()
    }
}
