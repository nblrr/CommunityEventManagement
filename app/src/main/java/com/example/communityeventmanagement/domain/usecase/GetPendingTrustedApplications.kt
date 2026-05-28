package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.entities.TrustedApplication
import com.example.communityeventmanagement.domain.repository.UserRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetPendingTrustedApplications @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(): StateFlow<List<TrustedApplication>> {
        return userRepository.trustedApplications
    }
}
