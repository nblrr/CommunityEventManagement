package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.entities.Organizer
import com.example.communityeventmanagement.domain.repository.UserRepository

/**
 * UseCase to register a user as an organizer.
 */
class RegisterOrganizer(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: String, organizer: Organizer): Result<Unit> {
        return userRepository.registerOrganizer(userId, organizer)
    }
}
