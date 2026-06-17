package com.example.communityeventmanagementsystem.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    val id: Long,
    val name: String,
    val email: String,
    val role: String,
    @SerializedName("is_blocked") val isBlocked: Boolean,
    @SerializedName("is_trusted") val isTrusted: Boolean,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("phone_number") val phoneNumber: String?,
    val gender: String?,
    val bio: String?,
    @SerializedName("birth_date") val birthDate: String?,
    @SerializedName("communities_count") val communitiesCount: Int? = 0,
    @SerializedName("events_count") val eventsCount: Int? = 0
)

data class BecomeOrganizerResponse(
    val message: String,
    val user: UserDto
)
