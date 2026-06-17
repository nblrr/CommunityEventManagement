package com.example.communityeventmanagementsystem.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ForumMessageDto(
    val id: Long,
    @SerializedName("community_id") val communityId: Long,
    @SerializedName("sender_id") val senderId: Long,
    val message: String,
    val sender: UserDto,
    @SerializedName("created_at") val createdAt: String
)

data class SendMessageRequest(
    val message: String
)
