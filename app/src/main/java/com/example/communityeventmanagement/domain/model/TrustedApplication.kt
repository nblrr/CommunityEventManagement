package com.example.communityeventmanagement.domain.model

data class TrustedApplication(
    val userId: String,
    val userName: String,
    val communityName: String,
    val reason: String,
    val experience: String,
    val status: ApplicationStatus = ApplicationStatus.PENDING,
)

