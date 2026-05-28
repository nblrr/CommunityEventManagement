package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.repository.UserRepository
import com.example.communityeventmanagement.domain.util.Resource
import javax.inject.Inject

class UpdateProfile @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(name: String, bio: String): Resource<Unit> {
        return repository.updateProfile(name, bio)
    }
}
