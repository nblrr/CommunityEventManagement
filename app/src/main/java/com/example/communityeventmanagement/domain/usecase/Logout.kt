package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.repository.UserRepository

/**
 * UseCase for user logout.
 */
class Logout(private val userRepository: UserRepository) {
    suspend operator fun invoke() {
        userRepository.logout()
    }
}
