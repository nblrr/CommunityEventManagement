package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.entities.User
import com.example.communityeventmanagement.domain.repository.UserRepository

/**
 * UseCase to handle user login.
 */
class Login(private val userRepository: UserRepository) {
    suspend operator fun invoke(email: String, password: String): Result<User> = userRepository.loginWithCredentials(email, password)
}
