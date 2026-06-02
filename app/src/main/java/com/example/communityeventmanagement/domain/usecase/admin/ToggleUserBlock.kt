package com.example.communityeventmanagement.domain.usecase.admin

import com.example.communityeventmanagement.domain.repository.UserRepository
import com.example.communityeventmanagement.util.Resource
import javax.inject.Inject

/**
 * UseCase to block or unblock a user.
 */
class ToggleUserBlock @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: String): Resource<Unit> {
        return userRepository.toggleUserBlock(userId)
    }
}

