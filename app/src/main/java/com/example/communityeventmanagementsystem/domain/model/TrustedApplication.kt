package com.example.communityeventmanagementsystem.domain.model

data class TrustedApplication(
    val id: Long,
    val userId: Long,
    val communityName: String,
    val reason: String?,
    val experience: String? = null,
    val status: String,
    val adminNotes: String?,
    val createdAt: String
)
