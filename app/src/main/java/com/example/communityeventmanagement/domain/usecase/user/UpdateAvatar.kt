package com.example.communityeventmanagement.domain.usecase.user

import com.example.communityeventmanagement.domain.repository.UserRepository
import com.example.communityeventmanagement.util.Resource
import javax.inject.Inject

/**
 * UseCase to update user's avatar URI.
 */
class UpdateAvatar @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(newUri: String?): Resource<Unit> {
        return userRepository.updateAvatar(newUri)
    }
}

