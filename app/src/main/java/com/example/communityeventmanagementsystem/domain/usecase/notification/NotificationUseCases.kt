package com.example.communityeventmanagementsystem.domain.usecase.notification

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.domain.model.Notification
import com.example.communityeventmanagementsystem.domain.repository.NotificationRepository
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(private val repository: NotificationRepository) {
    suspend operator fun invoke(): NetworkResult<List<Notification>> = repository.getNotifications()
}

class MarkNotificationAsReadUseCase @Inject constructor(private val repository: NotificationRepository) {
    suspend operator fun invoke(id: Long): NetworkResult<Unit> = repository.markAsRead(id)
}
