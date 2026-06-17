package com.example.communityeventmanagementsystem.presentation.notifications

import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.usecase.notification.GetNotificationsUseCase
import com.example.communityeventmanagementsystem.domain.usecase.notification.MarkNotificationAsReadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val markNotificationAsReadUseCase: MarkNotificationAsReadUseCase
) : BaseViewModel<NotificationContract.State, NotificationContract.Event, NotificationContract.Effect>() {

    private var pollingJob: Job? = null

    override fun createInitialState(): NotificationContract.State = NotificationContract.State()

    override fun handleEvent(event: NotificationContract.Event) {
        when (event) {
            is NotificationContract.Event.LoadNotifications -> {
                startPolling()
            }
            is NotificationContract.Event.OnNotificationClicked -> {
                markAsRead(event.id)
            }
        }
    }

    private fun startPolling() {
        if (pollingJob?.isActive == true) return
        pollingJob = viewModelScope.launch {
            while (isActive) {
                loadNotifications()
                delay(60000) // Poll every 60 seconds
            }
        }
    }

    private suspend fun loadNotifications() {
        when (val result = getNotificationsUseCase()) {
            is NetworkResult.Success -> {
                setState { copy(notifications = result.data, isLoading = false) }
            }
            is NetworkResult.Error -> {
                if (uiState.value.notifications.isEmpty()) {
                    setState { copy(error = result.message, isLoading = false) }
                }
            }
            is NetworkResult.Loading -> {
                if (uiState.value.notifications.isEmpty()) {
                    setState { copy(isLoading = true) }
                }
            }
        }
    }

    private fun markAsRead(id: Long) {
        viewModelScope.launch {
            markNotificationAsReadUseCase(id)
            loadNotifications() // Refresh list
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
