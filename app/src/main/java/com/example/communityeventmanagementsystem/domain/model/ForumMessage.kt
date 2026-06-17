package com.example.communityeventmanagementsystem.domain.model

data class ForumMessage(
    val id: Long,
    val communityId: Long,
    val senderId: Long,
    val message: String,
    val senderName: String,
    val createdAt: String
)
