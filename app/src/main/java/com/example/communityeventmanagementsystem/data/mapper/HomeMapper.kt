package com.example.communityeventmanagementsystem.data.mapper

import com.example.communityeventmanagementsystem.data.remote.dto.CategoryDto
import com.example.communityeventmanagementsystem.data.remote.dto.CommunityDto
import com.example.communityeventmanagementsystem.data.remote.dto.EventDto
import com.example.communityeventmanagementsystem.domain.model.Category
import com.example.communityeventmanagementsystem.domain.model.Community
import com.example.communityeventmanagementsystem.domain.model.Event

fun CategoryDto.toDomain() = Category(id, name, icon)
fun CommunityDto.toDomain() = Community(
    id = id,
    name = name,
    description = description,
    memberCount = memberCount,
    coverImageUrl = coverImageUrl,
    categoryName = category?.name,
    categoryId = categoryId,
    organizerName = organizer?.name,
    organizerId = organizerId,
    isOrganizerTrusted = organizer?.isTrusted ?: false,
    eventsCount = eventsCount ?: 0
)
fun EventDto.toDomain() = Event(
    id = id,
    title = title,
    description = description,
    eventDate = eventDate,
    attendeeCount = attendeeCount,
    maxAttendees = maxAttendees,
    status = status,
    coverImageUrl = coverImageUrl,
    categoryId = categoryId,
    categoryName = category?.name,
    location = location ?: "Zoom Meeting"
)

fun com.example.communityeventmanagementsystem.data.remote.dto.EventDetailResponse.toDomain() = Event(
    id = id,
    title = title,
    description = description,
    eventDate = eventDate,
    attendeeCount = attendeeCount,
    maxAttendees = maxAttendees,
    status = status,
    coverImageUrl = coverImageUrl,
    communityId = communityId,
    categoryId = categoryId,
    eventTime = eventTime,
    location = location,
    isOnline = isOnline,
    categoryName = categoryName,
    organizerName = organizerName,
    organizerId = organizerId,
    isOrganizerTrusted = isOrganizerTrusted,
    organizerImageUrl = organizerImageUrl,
    rating = rating ?: 0.0f,
    reviewCount = reviewCount ?: 0,
    endTime = endTime,
    locationName = locationName,
    price = price ?: 0.0
)
