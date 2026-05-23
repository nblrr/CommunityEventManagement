package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.repository.UserRepository

/**
 * UseCase to block or unblock a user.
 */
class ToggleUserBlock(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: String): Result<Unit> {
        return userRepository.toggleUserBlock(userId)
    }
}
