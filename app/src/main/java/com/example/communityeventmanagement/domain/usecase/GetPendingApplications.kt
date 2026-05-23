package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.entities.TrustedApplication
import com.example.communityeventmanagement.domain.repository.UserRepository
import kotlinx.coroutines.flow.StateFlow

/**
 * UseCase to get pending trusted organizer applications.
 */
class GetPendingApplications(private val userRepository: UserRepository) {
    operator fun invoke(): StateFlow<List<TrustedApplication>> {
        return userRepository.trustedApplications
    }
}
