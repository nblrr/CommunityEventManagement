package com.example.communityeventmanagementsystem.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.network.ErrorHandler
import com.example.communityeventmanagementsystem.data.remote.api.MediaApi
import com.example.communityeventmanagementsystem.data.remote.dto.UploadResponse
import com.example.communityeventmanagementsystem.domain.repository.MediaRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import okio.source
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: MediaApi
) : MediaRepository {

    override suspend fun uploadImage(uri: Uri, type: String): NetworkResult<UploadResponse> {
        return try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri) ?: "image/*"
            val fileName = getFileName(context, uri) ?: "image_upload"

            val requestFile = object : RequestBody() {
                override fun contentType() = mimeType.toMediaTypeOrNull()
                override fun contentLength() = getFileSize(uri)
                override fun writeTo(sink: BufferedSink) {
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        input.source().use { source ->
                            sink.writeAll(source)
                        }
                    }
                }
            }

            val imagePart = MultipartBody.Part.createFormData("image", fileName, requestFile)
            val typePart = type.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = api.uploadImage(imagePart, typePart)
            NetworkResult.Success(response)
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    private fun getFileSize(uri: Uri): Long {
        return context.contentResolver.openAssetFileDescriptor(uri, "r")?.use {
            it.length
        } ?: -1L
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        var name: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) {
                        name = it.getString(index)
                    }
                }
            }
        }
        if (name == null) {
            name = uri.path
            val cut = name?.lastIndexOf('/') ?: -1
            if (cut != -1) {
                name = name?.substring(cut + 1)
            }
        }
        return name
    }
}
