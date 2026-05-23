package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.repository.UserRepository

class UpdateProfile(private val repository: UserRepository) {
    suspend operator fun invoke(name: String, bio: String): Result<Unit> {
        return repository.updateProfile(name, bio)
    }
}
