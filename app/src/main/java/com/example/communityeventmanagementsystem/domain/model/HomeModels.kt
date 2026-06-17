package com.example.communityeventmanagementsystem.domain.model

data class Category(
    val id: Long,
    val name: String,
    val icon: String?
)

data class Community(
    val id: Long,
    val name: String,
    val description: String?,
    val memberCount: Int,
    val coverImageUrl: String? = null,
    val categoryName: String? = null,
    val categoryId: Long = 1L,
    val organizerName: String? = null,
    val eventsCount: Int = 0
)

data class Event(
    val id: Long,
    val title: String,
    val description: String?,
    val eventDate: String,
    val attendeeCount: Int,
    val maxAttendees: Int,
    val status: String,
    val coverImageUrl: String?,
    val communityId: Long = 1L,
    val categoryId: Long = 1L,
    val eventTime: String = "12:00",
    val location: String = "Zoom Meeting",
    val isOnline: Boolean = true,
    
    // TYPE A fields (Now integrated with API)
    val categoryName: String? = null,
    val organizerName: String? = null,
    val organizerImageUrl: String? = null,
    val rating: Float = 0.0f,
    val reviewCount: Int = 0,
    val endTime: String? = null,
    val locationName: String? = null,
    val price: Double = 0.0
)

// TYPE B — Computed locally from existing fields
val Event.isFree: Boolean
    get() = price == 0.0

val Event.isFillingFast: Boolean
    get() = maxAttendees > 0 && (attendeeCount.toFloat() / maxAttendees) > 0.8f

val Event.isUpcoming: Boolean
    get() = status == "upcoming"
