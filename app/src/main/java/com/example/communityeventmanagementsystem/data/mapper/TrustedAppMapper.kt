package com.example.communityeventmanagementsystem.data.mapper

import com.example.communityeventmanagementsystem.data.remote.dto.TrustedAppDto
import com.example.communityeventmanagementsystem.domain.model.TrustedApplication

fun TrustedAppDto.toDomain() = TrustedApplication(
    id = id,
    userId = userId,
    communityName = communityName,
    reason = reason,
    experience = experience,
    status = status,
    adminNotes = adminNotes,
    createdAt = createdAt
)
