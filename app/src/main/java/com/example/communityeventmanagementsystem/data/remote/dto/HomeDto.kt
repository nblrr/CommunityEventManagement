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
    @SerializedName("events_count") val eventsCount: Int? = null,
    val events: List<EventDto>? = null
)

data class EventDto(
    val id: Long,
    val title: String,
    val description: String?,
    @SerializedName("event_date") val eventDate: String,
    val location: String?,
    @SerializedName("attendee_count") val attendeeCount: Int,
    @SerializedName("max_attendees") val maxAttendees: Int,
    @SerializedName("status", alternate = ["calculated_status"]) val status: String? = "UPCOMING",
    @SerializedName("cover_image_url") val coverImageUrl: String?,
    @SerializedName("category_id") val categoryId: Long = 1L,
    val category: CategoryDto? = null,
    @SerializedName("event_time") val eventTime: String? = null,
    @SerializedName("is_online") val isOnline: Boolean? = null,
    @SerializedName("community_id") val communityId: Long? = null
)
