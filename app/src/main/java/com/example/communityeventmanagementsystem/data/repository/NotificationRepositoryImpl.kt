package com.example.communityeventmanagementsystem.data.repository

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.network.ErrorHandler
import com.example.communityeventmanagementsystem.data.mapper.toDomain
import com.example.communityeventmanagementsystem.data.remote.dto.FcmTokenRequest
import com.example.communityeventmanagementsystem.data.remote.api.NotificationApi
import com.example.communityeventmanagementsystem.domain.model.Notification
import com.example.communityeventmanagementsystem.domain.repository.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val api: NotificationApi
) : NotificationRepository {

    override suspend fun getNotifications(): NetworkResult<List<Notification>> {
        return try {
            val response = api.getNotifications()
            NetworkResult.Success(response.data.map { it.toDomain() })
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun markAsRead(id: Long): NetworkResult<Unit> {
        return try {
            api.markAsRead(id)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun updateFcmToken(fcmToken: String): NetworkResult<Unit> {
        return try {
            api.updateFcmToken(FcmTokenRequest(fcmToken))
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }
}
