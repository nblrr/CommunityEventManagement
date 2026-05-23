package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.repository.UserRepository

/**
 * UseCase to reject a trusted organizer application.
 */
class RejectApplication(private val userRepository: UserRepository) {
    suspend operator fun invoke(applicationId: String): Result<Unit> {
        return userRepository.handleTrustedApplication(applicationId, false)
    }
}
