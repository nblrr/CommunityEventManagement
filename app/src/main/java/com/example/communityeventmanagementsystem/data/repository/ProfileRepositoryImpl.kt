package com.example.communityeventmanagementsystem.data.repository

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.datastore.DataStoreManager
import com.example.communityeventmanagementsystem.data.mapper.toDomain
import com.example.communityeventmanagementsystem.data.remote.api.ProfileApi
import com.example.communityeventmanagementsystem.data.remote.dto.UserDto
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
            dataStoreManager.saveRole(response.role)
            NetworkResult.Success(response.toDomain())
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun updateProfile(user: User): NetworkResult<User> {
        return try {
            val userDto = UserDto(
                id = user.id,
                name = user.name,
                email = user.email,
                role = user.role,
                isBlocked = user.isBlocked,
                isTrusted = user.isTrusted,
                avatarUrl = user.avatarUrl,
                phoneNumber = user.phoneNumber,
                gender = user.gender,
                bio = user.bio,
                birthDate = user.birthDate
            )
            val response = api.updateProfile(userDto)
            dataStoreManager.saveUserData(gson.toJson(response))
            dataStoreManager.saveRole(response.role)
            NetworkResult.Success(response.toDomain())
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun uploadAvatar(file: File): NetworkResult<User> {
        return try {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("avatar", file.name, requestFile)
            val response = api.uploadAvatar(body)
            dataStoreManager.saveUserData(gson.toJson(response))
            dataStoreManager.saveRole(response.role)
            NetworkResult.Success(response.toDomain())
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun becomeOrganizer(): NetworkResult<User> {
        return try {
            val response = api.becomeOrganizer()
            dataStoreManager.saveUserData(gson.toJson(response.user))
            dataStoreManager.saveRole(response.user.role)
            NetworkResult.Success(response.user.toDomain())
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An unknown error occurred")
        }
    }
}
