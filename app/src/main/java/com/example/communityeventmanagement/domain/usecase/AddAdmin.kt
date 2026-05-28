package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.entities.User
import com.example.communityeventmanagement.domain.repository.UserRepository
import com.example.communityeventmanagement.domain.util.Resource
import javax.inject.Inject

/**
 * UseCase to add a new administrator.
 */
class AddAdmin @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(name: String, email: String, password: String): Resource<User> {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            return Resource.Error("All fields are required")
        }
        return userRepository.addAdmin(name, email, password)
    }
}
