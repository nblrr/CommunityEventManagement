package com.example.communityeventmanagementsystem.data.mapper

import com.example.communityeventmanagementsystem.data.remote.dto.NotificationDto
import com.example.communityeventmanagementsystem.domain.model.Notification

fun NotificationDto.toDomain() = Notification(
    id = id,
    title = title,
    message = message,
    type = type,
    isRead = isRead,
    createdAt = createdAt
)
