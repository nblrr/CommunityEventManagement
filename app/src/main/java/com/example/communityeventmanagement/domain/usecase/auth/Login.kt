package com.example.communityeventmanagement.domain.usecase.auth

import com.example.communityeventmanagement.domain.model.User
import com.example.communityeventmanagement.domain.repository.UserRepository
import com.example.communityeventmanagement.util.Resource
import javax.inject.Inject

/**
 * UseCase to handle user login.
 */
class Login @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(email: String, password: String): Resource<User> = userRepository.loginWithCredentials(email, password)
}

