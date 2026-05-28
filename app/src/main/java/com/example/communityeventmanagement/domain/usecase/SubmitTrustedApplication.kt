package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.repository.UserRepository
import com.example.communityeventmanagement.domain.util.Resource
import javax.inject.Inject

class SubmitTrustedApplication @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(communityName: String, reason: String, experience: String): Resource<Unit> {
        return userRepository.submitTrustedApplication(communityName, reason, experience)
    }
}
