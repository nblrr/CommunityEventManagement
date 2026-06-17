package com.example.communityeventmanagementsystem.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NotificationDto(
    val id: Long,
    val title: String,
    val message: String,
    val type: String,
    @SerializedName("is_read") val isRead: Boolean,
    @SerializedName("created_at") val createdAt: String
)
