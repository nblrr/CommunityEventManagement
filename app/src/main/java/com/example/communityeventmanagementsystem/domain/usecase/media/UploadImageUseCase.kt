package com.example.communityeventmanagementsystem.domain.usecase.media

import android.net.Uri
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.data.remote.dto.UploadResponse
import com.example.communityeventmanagementsystem.domain.repository.MediaRepository
import javax.inject.Inject

class UploadImageUseCase @Inject constructor(
    private val repository: MediaRepository
) {
    suspend operator fun invoke(uri: Uri, type: String): NetworkResult<UploadResponse> {
        return repository.uploadImage(uri, type)
    }
}
