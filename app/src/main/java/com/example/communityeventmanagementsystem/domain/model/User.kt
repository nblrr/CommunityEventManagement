package com.example.communityeventmanagementsystem.domain.model

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val role: String,
    val isBlocked: Boolean,
    val isTrusted: Boolean,
    val avatarUrl: String?,
    val phoneNumber: String?,
    val gender: String?,
    val bio: String?,
    val birthDate: String?,
    val communitiesCount: Int = 0,
    val eventsCount: Int = 0
)
