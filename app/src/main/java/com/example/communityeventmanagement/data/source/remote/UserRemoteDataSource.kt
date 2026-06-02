package com.example.communityeventmanagement.data.source.remote

import com.example.communityeventmanagement.data.dto.UserDto
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getCurrentUser(): UserDto? {
        val response = apiService.getCurrentUser()
        return if (response.isSuccessful) response.body() else null
    }
}
