package com.example.communityeventmanagement.domain.usecase.auth

import com.example.communityeventmanagement.domain.model.User
import com.example.communityeventmanagement.domain.repository.UserRepository
import com.example.communityeventmanagement.util.Resource
import javax.inject.Inject

/**
 * UseCase to register a new user.
 */
class Register @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(name: String, email: String, password: String): Resource<User> = userRepository.register(name, email, password)
}

