package com.example.communityeventmanagement.data.mapper

import com.example.communityeventmanagement.data.dto.EventDto
import com.example.communityeventmanagement.data.dto.RatingDto
import com.example.communityeventmanagement.domain.entities.Event
import com.example.communityeventmanagement.domain.entities.Rating

fun EventDto.toDomain(): Event {
    return Event(
        id = this.id ?: 0,
        title = this.title ?: "Untitled Event",
        description = this.description ?: "",
        date = this.date ?: "",
        time = this.time ?: "",
        location = this.location ?: "",
        category = this.category ?: "General",
        maxAttendees = this.maxAttendees ?: 0,
        coverImageUri = this.coverImageUri,
        communityId = this.communityId ?: 0,
        registeredUserIds = this.registeredUserIds ?: emptyList(),
        galleryImages = this.galleryImages ?: emptyList(),
        ratings = (this.ratings ?: emptyList()).map { it.toDomain() },
        attendeeCount = this.attendeeCount ?: 0
    )
}

fun RatingDto.toDomain(): Rating {
    return Rating(
        userId = this.userId ?: "",
        userName = this.userName ?: "Unknown",
        score = this.score ?: 0,
        comment = this.comment ?: "",
        date = this.date ?: ""
    )
}

fun Event.toDto(): EventDto {
    return EventDto(
        id = this.id,
        title = this.title,
        description = this.description,
        date = this.date,
        time = this.time,
        location = this.location,
        category = this.category,
        maxAttendees = this.maxAttendees,
        coverImageUri = this.coverImageUri,
        communityId = this.communityId,
        registeredUserIds = this.registeredUserIds,
        galleryImages = this.galleryImages,
        ratings = this.ratings.map { it.toDto() },
        attendeeCount = this.attendeeCount
    )
}

fun Rating.toDto(): RatingDto {
    return RatingDto(
        userId = this.userId,
        userName = this.userName,
        score = this.score,
        comment = this.comment,
        date = this.date
    )
}
