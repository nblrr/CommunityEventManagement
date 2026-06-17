package com.example.communityeventmanagementsystem.data.repository

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.datastore.DataStoreManager
import com.example.communityeventmanagementsystem.core.network.ErrorHandler
import com.example.communityeventmanagementsystem.data.mapper.toDomain
import com.example.communityeventmanagementsystem.data.mapper.toDto
import com.example.communityeventmanagementsystem.data.remote.api.ProfileApi
import com.example.communityeventmanagementsystem.domain.model.User
import com.example.communityeventmanagementsystem.domain.repository.ProfileRepository
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val api: ProfileApi,
    private val dataStoreManager: DataStoreManager,
    private val gson: Gson
) : ProfileRepository {

    override suspend fun getProfile(): NetworkResult<User> {
        return try {
            val response = api.getProfile()
            dataStoreManager.saveUserData(gson.toJson(response))
            NetworkResult.Success(response.toDomain())
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun updateProfile(user: User): NetworkResult<User> {
        return try {
            val response = api.updateProfile(user.toDto())
            dataStoreManager.saveUserData(gson.toJson(response))
            NetworkResult.Success(response.toDomain())
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun uploadAvatar(file: File): NetworkResult<User> {
        return try {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("avatar", file.name, requestFile)
            val response = api.uploadAvatar(body)
            dataStoreManager.saveUserData(gson.toJson(response))
            NetworkResult.Success(response.toDomain())
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun becomeOrganizer(): NetworkResult<User> {
        return try {
            val response = api.becomeOrganizer()
            dataStoreManager.saveRole("ORGANIZER")
            dataStoreManager.saveUserData(gson.toJson(response.user))
            NetworkResult.Success(response.user.toDomain())
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }
}
