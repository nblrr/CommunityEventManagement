package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.entities.User
import com.example.communityeventmanagement.domain.repository.UserRepository
import com.example.communityeventmanagement.domain.util.Resource
import javax.inject.Inject

/**
 * UseCase to register a new user.
 */
class Register @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(name: String, email: String, password: String): Resource<User> = userRepository.register(name, email, password)
}
