package com.example.communityeventmanagement.domain.entities

data class Event(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val time: String = "",
    val location: String,
    val category: String,
    val maxAttendees: Int = 0,
    val coverImageUri: String? = null,
    val communityId: Int = 0,
    val registeredUserIds: List<String> = emptyList(),
    val galleryImages: List<String> = emptyList(),
    val ratings: List<Rating> = emptyList(),
    val attendeeCount: Int = 0
)
