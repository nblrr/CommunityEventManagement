package com.example.communityeventmanagementsystem.data.mapper

import com.example.communityeventmanagementsystem.data.remote.dto.UserDto
import com.example.communityeventmanagementsystem.domain.model.User

fun UserDto.toDomain(): User {
    return User(
        id = id,
        name = name,
        email = email,
        role = role,
        isBlocked = isBlocked,
        isTrusted = isTrusted,
        avatarUrl = avatarUrl,
        phoneNumber = phoneNumber,
        gender = gender,
        bio = bio,
        birthDate = birthDate,
        communitiesCount = communitiesCount ?: 0,
        eventsCount = eventsCount ?: 0
    )
}
