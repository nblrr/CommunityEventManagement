package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.repository.CommunityRepository
import com.example.communityeventmanagement.domain.repository.UserRepository
import javax.inject.Inject

/**
 * UseCase to initialize application data (users, communities, and user participation).
 */
class InitializeApp @Inject constructor(
    private val userRepository: UserRepository,
    private val communityRepository: CommunityRepository
) {
    suspend operator fun invoke() {
        userRepository.initialize()
        val allUsers = userRepository.users.value
        communityRepository.loadCommunities(allUsers)
        
        val currentUser = userRepository.currentUser.value
        if (currentUser != null) {
            communityRepository.refreshUserParticipation(currentUser)
        }
    }
}
