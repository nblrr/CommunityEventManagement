package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.entities.User
import com.example.communityeventmanagement.domain.repository.CommunityRepository

/**
 * UseCase to refresh participation data for the current user.
 */
class RefreshData(private val repository: CommunityRepository) {
    suspend operator fun invoke(currentUser: User?) {
        repository.refreshUserParticipation(currentUser)
    }
}
