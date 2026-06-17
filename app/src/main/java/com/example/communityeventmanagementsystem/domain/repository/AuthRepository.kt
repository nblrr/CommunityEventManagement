package com.example.communityeventmanagementsystem.domain.repository

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.data.remote.dto.LoginRequest
import com.example.communityeventmanagementsystem.data.remote.dto.RegisterRequest
import com.example.communityeventmanagementsystem.domain.model.User

interface AuthRepository {
    suspend fun login(request: LoginRequest): NetworkResult<User>
    suspend fun register(request: RegisterRequest): NetworkResult<User>
    suspend fun logout(): NetworkResult<Unit>
}
