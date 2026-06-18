package com.example.communityeventmanagementsystem.domain.repository

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.domain.model.Notification

interface NotificationRepository {
    suspend fun getNotifications(): NetworkResult<List<Notification>>
    suspend fun markAsRead(id: Long): NetworkResult<Unit>
    suspend fun updateFcmToken(fcmToken: String): NetworkResult<Unit>
}
