package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.repository.UserRepository

/**
 * UseCase to update user's avatar URI.
 */
class UpdateAvatar(private val userRepository: UserRepository) {
    suspend operator fun invoke(newUri: String?): Result<Unit> {
        return userRepository.updateAvatar(newUri)
    }
}
