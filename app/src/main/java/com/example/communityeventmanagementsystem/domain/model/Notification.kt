package com.example.communityeventmanagementsystem.domain.model

data class Notification(
    val id: Long,
    val title: String,
    val message: String,
    val type: String,
    val isRead: Boolean,
    val createdAt: String
)
