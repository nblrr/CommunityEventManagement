package com.example.communityeventmanagementsystem.data.remote.api

import com.example.communityeventmanagementsystem.data.remote.dto.AuthResponse
import com.example.communityeventmanagementsystem.data.remote.dto.LoginRequest
import com.example.communityeventmanagementsystem.data.remote.dto.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("logout")
    suspend fun logout()
}
