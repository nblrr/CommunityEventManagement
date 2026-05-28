package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.entities.User
import com.example.communityeventmanagement.domain.repository.UserRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * UseCase to get the currently logged-in user.
 */
class GetCurrentUser @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(): StateFlow<User?> = userRepository.currentUser
}
