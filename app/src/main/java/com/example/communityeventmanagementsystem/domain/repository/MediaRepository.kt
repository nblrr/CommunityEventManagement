package com.example.communityeventmanagementsystem.domain.repository

import android.net.Uri
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.data.remote.dto.UploadResponse

interface MediaRepository {
    suspend fun uploadImage(uri: Uri, type: String): NetworkResult<UploadResponse>
}
