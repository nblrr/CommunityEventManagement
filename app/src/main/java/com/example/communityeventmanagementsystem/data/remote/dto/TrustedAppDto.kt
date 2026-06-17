package com.example.communityeventmanagementsystem.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TrustedAppDto(
    val id: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("community_name") val communityName: String,
    val reason: String?,
    val experience: String?,
    val status: String,
    @SerializedName("admin_notes") val adminNotes: String?,
    @SerializedName("created_at") val createdAt: String
)

data class SubmitTrustedAppRequest(
    @SerializedName("community_name") val communityName: String,
    val reason: String,
    val experience: String?
)
