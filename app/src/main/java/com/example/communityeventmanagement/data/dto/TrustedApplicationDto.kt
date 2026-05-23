package com.example.communityeventmanagement.data.dto

import com.google.gson.annotations.SerializedName

data class TrustedApplicationDto(
    @SerializedName("user_id") val userId: String?,
    @SerializedName("user_name") val userName: String?,
    @SerializedName("community_name") val communityName: String?,
    @SerializedName("reason") val reason: String?,
    @SerializedName("experience") val experience: String?,
    @SerializedName("status") val status: String?
)
