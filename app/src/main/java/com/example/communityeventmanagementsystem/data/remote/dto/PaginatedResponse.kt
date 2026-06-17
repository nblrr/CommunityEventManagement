package com.example.communityeventmanagementsystem.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PaginatedResponse<T>(
    val data: List<T>,
    @SerializedName("current_page") val current_page: Int,
    @SerializedName("last_page") val last_page: Int,
    val total: Int
)
