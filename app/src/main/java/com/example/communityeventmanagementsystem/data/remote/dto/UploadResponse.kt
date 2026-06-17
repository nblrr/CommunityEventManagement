package com.example.communityeventmanagementsystem.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UploadResponse(
    @SerializedName("url") val url: String,
    @SerializedName("path") val path: String,
    @SerializedName("bucket") val bucket: String,
    @SerializedName("type") val type: String
)
