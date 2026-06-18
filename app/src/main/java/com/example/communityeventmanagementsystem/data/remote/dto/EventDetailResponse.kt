package com.example.communityeventmanagementsystem.data.remote.dto

import com.google.gson.annotations.SerializedName

data class EventDetailResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("event_date") val eventDate: String,
    @SerializedName("attendee_count") val attendeeCount: Int,
    @SerializedName("max_attendees") val maxAttendees: Int,
    @SerializedName("status") val status: String,
    @SerializedName("cover_image_url") val coverImageUrl: String?,
    @SerializedName("community_id") val communityId: Long = 1L,
    @SerializedName("category_id") val categoryId: Long = 1L,
    @SerializedName("event_time") val eventTime: String = "12:00",
    @SerializedName("location") val location: String = "Zoom Meeting",
    @SerializedName("is_online") val isOnline: Boolean = true,
    
    // NEW fields from Stitch UI
    @SerializedName("category_name") val categoryName: String? = null,
    @SerializedName("organizer_name") val organizerName: String? = null,
    @SerializedName("organizer_id") val organizerId: Long = 0L,
    @SerializedName("is_organizer_trusted") val isOrganizerTrusted: Boolean = false,
    @SerializedName("organizer_image_url") val organizerImageUrl: String? = null,
    @SerializedName("rating") val rating: Float? = null,
    @SerializedName("review_count") val reviewCount: Int? = null,
    @SerializedName("end_time") val endTime: String? = null,
    @SerializedName("location_name") val locationName: String? = null,
    @SerializedName("price") val price: Double? = null
)
