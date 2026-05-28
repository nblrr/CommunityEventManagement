package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.repository.UserRepository
import com.example.communityeventmanagement.domain.util.Resource
import javax.inject.Inject

class RejectTrustedApplication @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: String): Resource<Unit> {
        return userRepository.handleTrustedApplication(userId, false)
    }
}
