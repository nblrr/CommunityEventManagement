package com.example.communityeventmanagementsystem.data.remote.dto

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    @com.google.gson.annotations.SerializedName("password_confirmation") val passwordConfirmation: String
)

data class AuthResponse(
    val token: String,
    val user: UserDto
)
