package com.example.communityeventmanagementsystem.data.repository

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.datastore.DataStoreManager
import com.example.communityeventmanagementsystem.data.mapper.toDomain
import com.example.communityeventmanagementsystem.data.remote.api.AuthApi
import com.example.communityeventmanagementsystem.data.remote.dto.LoginRequest
import com.example.communityeventmanagementsystem.data.remote.dto.RegisterRequest
import com.example.communityeventmanagementsystem.domain.model.User
import com.example.communityeventmanagementsystem.domain.repository.AuthRepository
import com.google.gson.Gson
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val dataStoreManager: DataStoreManager,
    private val gson: Gson
) : AuthRepository {

    override suspend fun login(request: LoginRequest): NetworkResult<User> {
        return try {
            val response = api.login(request)
            dataStoreManager.saveToken(response.token)
            dataStoreManager.saveRole(response.user.role)
            dataStoreManager.saveUserData(gson.toJson(response.user))
            NetworkResult.Success(response.user.toDomain())
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun register(request: RegisterRequest): NetworkResult<User> {
        return try {
            val response = api.register(request)
            dataStoreManager.saveToken(response.token)
            dataStoreManager.saveRole(response.user.role)
            dataStoreManager.saveUserData(gson.toJson(response.user))
            NetworkResult.Success(response.user.toDomain())
        } catch (e: Exception) {
            e.printStackTrace()

            NetworkResult.Error(
                e.message ?: "An unknown error occurred"
            )
        }
    }

    override suspend fun logout(): NetworkResult<Unit> {
        return try {
            api.logout()
            dataStoreManager.clearSession()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            dataStoreManager.clearSession() // Still clear local session
            NetworkResult.Success(Unit)
        }
    }
}
