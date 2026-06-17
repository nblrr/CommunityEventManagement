package com.example.communityeventmanagementsystem.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateEventRequest(
    @SerializedName("community_id") val communityId: Long,
    @SerializedName("category_id") val categoryId: Long,
    val title: String,
    val description: String,
    @SerializedName("event_date") val eventDate: String,
    @SerializedName("event_time") val eventTime: String,
    val location: String,
    @SerializedName("max_attendees") val maxAttendees: Int,
    @SerializedName("is_online") val isOnline: Boolean,
    @SerializedName("cover_image_url") val coverImageUrl: String?
)
