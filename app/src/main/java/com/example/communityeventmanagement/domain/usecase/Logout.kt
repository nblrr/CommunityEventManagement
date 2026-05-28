package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.repository.UserRepository
import javax.inject.Inject

/**
 * UseCase for user logout.
 */
class Logout @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke() {
        userRepository.logout()
    }
}
