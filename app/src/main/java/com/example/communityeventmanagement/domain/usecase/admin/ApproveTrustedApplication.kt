package com.example.communityeventmanagement.domain.usecase.admin

import com.example.communityeventmanagement.domain.repository.UserRepository
import com.example.communityeventmanagement.util.Resource
import javax.inject.Inject

class ApproveTrustedApplication @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: String): Resource<Unit> {
        return userRepository.handleTrustedApplication(userId, true)
    }
}

