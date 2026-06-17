package com.example.communityeventmanagementsystem.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateCommunityRequest(
    val name: String,
    val description: String,
    @SerializedName("category_id") val categoryId: Long,
    @SerializedName("cover_image_url") val coverImageUrl: String?
)
