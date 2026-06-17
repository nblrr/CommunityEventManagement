package com.example.communityeventmanagementsystem.data.mapper

import com.example.communityeventmanagementsystem.data.remote.dto.ForumMessageDto
import com.example.communityeventmanagementsystem.domain.model.ForumMessage

fun ForumMessageDto.toDomain() = ForumMessage(
    id = id,
    communityId = communityId,
    senderId = senderId,
    message = message,
    senderName = sender.name,
    createdAt = createdAt
)
