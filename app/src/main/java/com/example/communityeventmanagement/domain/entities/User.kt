package com.example.communityeventmanagement.domain.entities

data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String = "",
    val bio: String = "",
    val avatarUri: String? = null,
    val role: UserRole = UserRole.USER,
    val isBlocked: Boolean = false,
    val isTrusted: Boolean = false,
    val trustedApplicationStatus: ApplicationStatus = ApplicationStatus.NONE,
    val organizer: Organizer? = null,
)

data class Organizer(
    val communityName: String,
    val personInCharge: String,
    val description: String,
    val phone: String,
)
