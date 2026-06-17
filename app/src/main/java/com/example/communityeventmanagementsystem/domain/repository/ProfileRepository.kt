package com.example.communityeventmanagementsystem.domain.repository

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.domain.model.User
import java.io.File

interface ProfileRepository {
    suspend fun getProfile(): NetworkResult<User>
    suspend fun updateProfile(user: User): NetworkResult<User>
    suspend fun uploadAvatar(file: File): NetworkResult<User>
    suspend fun becomeOrganizer(): NetworkResult<User>
}
