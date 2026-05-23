package com.example.communityeventmanagement.data.dto

import com.google.gson.annotations.SerializedName

data class EventDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("time") val time: String? = "",
    @SerializedName("location") val location: String?,
    @SerializedName("category") val category: String?,
    @SerializedName("max_attendees") val maxAttendees: Int? = 0,
    @SerializedName("cover_image_uri") val coverImageUri: String? = null,
    @SerializedName("community_id") val communityId: Int? = 0,
    @SerializedName("registered_user_ids") val registeredUserIds: List<String>? = emptyList(),
    @SerializedName("gallery_images") val galleryImages: List<String>? = emptyList(),
    @SerializedName("ratings") val ratings: List<RatingDto>? = emptyList(),
    @SerializedName("attendee_count") val attendeeCount: Int? = 0
)

data class RatingDto(
    @SerializedName("user_id") val userId: String?,
    @SerializedName("user_name") val userName: String?,
    @SerializedName("score") val score: Int?,
    @SerializedName("comment") val comment: String?,
    @SerializedName("date") val date: String?
)
