package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.entities.Organizer
import com.example.communityeventmanagement.domain.repository.UserRepository
import com.example.communityeventmanagement.domain.util.Resource
import javax.inject.Inject

/**
 * UseCase to register a user as an organizer.
 */
class RegisterOrganizer @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: String, organizer: Organizer): Resource<Unit> {
        return userRepository.registerOrganizer(userId, organizer)
    }
}
