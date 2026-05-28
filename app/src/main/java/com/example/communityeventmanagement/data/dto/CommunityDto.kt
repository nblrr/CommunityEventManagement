package com.example.communityeventmanagement.data.dto

import com.google.gson.annotations.SerializedName

data class CommunityDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("category") val category: String?,
    @SerializedName("cover_image_uri") val coverImageUri: String? = null,
    @SerializedName("organizer_id") val organizerId: String?,
    @SerializedName("organizer_name") val organizerName: String?,
    @SerializedName("member_count") val memberCount: Int? = null,
    @SerializedName("member_ids") val memberIds: List<String>? = emptyList(),
    @SerializedName("events") val events: List<EventDto>? = emptyList()
)

data class ForumMessageDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("community_id") val communityId: Int? = null,
    @SerializedName("sender") val sender: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("time") val time: String?,
    @SerializedName("avatar_initials") val avatarInitials: String?
)
