package com.example.communityeventmanagementsystem.presentation.notifications

import com.example.communityeventmanagementsystem.domain.model.Notification

class NotificationContract {
    data class State(
        val isLoading: Boolean = false,
        val notifications: List<Notification> = emptyList(),
        val error: String? = null
    )

    sealed class Event {
        object LoadNotifications : Event()
        data class OnNotificationClicked(val id: Long) : Event()
    }

    sealed class Effect {
        // No specific effects needed for now
    }
}
