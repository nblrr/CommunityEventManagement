package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.entities.User
import com.example.communityeventmanagement.domain.repository.UserRepository

/**
 * UseCase to register a new user.
 */
class Register(private val userRepository: UserRepository) {
    suspend operator fun invoke(name: String, email: String, password: String): Result<User> = userRepository.register(name, email, password)
}
