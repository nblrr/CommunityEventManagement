package com.example.communityeventmanagementsystem.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CategoryDto(
    val id: Long,
    val name: String,
    val icon: String?
)

data class CommunityDto(
    val id: Long,
    val name: String,
    val description: String?,
    @SerializedName("organizer_id") val organizerId: Long,
    @SerializedName("category_id") val categoryId: Long,
    @SerializedName("member_count") val memberCount: Int,
    @SerializedName("cover_image_url") val coverImageUrl: String? = null,
    val category: CategoryDto? = null,
    val organizer: UserDto? = null,
    @SerializedName("events_count") val eventsCount: Int? = null
)

data class EventDto(
    val id: Long,
    val title: String,
    val description: String?,
    @SerializedName("event_date") val eventDate: String,
    val location: String?,
    @SerializedName("attendee_count") val attendeeCount: Int,
    @SerializedName("max_attendees") val maxAttendees: Int,
    val status: String,
    @SerializedName("cover_image_url") val coverImageUrl: String?,
    @SerializedName("category_id") val categoryId: Long = 1L,
    val category: CategoryDto? = null
)
