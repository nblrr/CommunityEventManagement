package com.example.communityeventmanagementsystem.domain.usecase.profile

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.domain.model.User
import com.example.communityeventmanagementsystem.domain.repository.ProfileRepository
import java.io.File
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(): NetworkResult<User> = repository.getProfile()
}

class UpdateProfileUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(user: User): NetworkResult<User> = repository.updateProfile(user)
}

class UploadAvatarUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(file: File): NetworkResult<User> = repository.uploadAvatar(file)
}

class BecomeOrganizerUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(): NetworkResult<User> = repository.becomeOrganizer()
}
