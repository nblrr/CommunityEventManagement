package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.entities.Community
import com.example.communityeventmanagement.domain.repository.UserRepository

/**
 * UseCase to submit an application for Trusted Organizer status.
 */
class SubmitTrustedApplication(private val userRepository: UserRepository) {
    suspend operator fun invoke(communities: List<Community>, reason: String, experience: String): Result<Unit> {
        return userRepository.submitTrustedApplication(communities, reason, experience)
    }
}
