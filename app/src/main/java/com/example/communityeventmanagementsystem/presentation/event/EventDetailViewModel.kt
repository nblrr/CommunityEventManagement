package com.example.communityeventmanagementsystem.presentation.event

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.usecase.event.GetEventDetailUseCase
import com.example.communityeventmanagementsystem.domain.usecase.event.GetMyRegisteredEventsUseCase
import com.example.communityeventmanagementsystem.domain.usecase.event.RegisterToEventUseCase
import com.example.communityeventmanagementsystem.domain.usecase.event.UnregisterFromEventUseCase
import com.example.communityeventmanagementsystem.core.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val getEventDetailUseCase: GetEventDetailUseCase,
    private val registerToEventUseCase: RegisterToEventUseCase,
    private val unregisterFromEventUseCase: UnregisterFromEventUseCase,
    private val getMyRegisteredEventsUseCase: GetMyRegisteredEventsUseCase,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<EventDetailContract.State, EventDetailContract.Event, EventDetailContract.Effect>() {

    init {
        savedStateHandle.get<Long>("id")?.let { id ->
            loadDetail(id, forceRefresh = false)
        }
    }

    override fun createInitialState(): EventDetailContract.State = EventDetailContract.State()

    override fun handleEvent(event: EventDetailContract.Event) {
        when (event) {
            is EventDetailContract.Event.LoadDetail -> loadDetail(event.id, forceRefresh = false)
            is EventDetailContract.Event.Register -> register()
            is EventDetailContract.Event.Unregister -> unregister()
            is EventDetailContract.Event.Logout -> logout()
        }
    }

    private fun logout() {
        viewModelScope.launch {
            sessionManager.logout()
            setEffect { EventDetailContract.Effect.NavigateToLogin }
        }
    }

    private fun loadDetail(id: Long, forceRefresh: Boolean) {
        if (!forceRefresh && uiState.value.event?.id == id && !uiState.value.isLoading) return
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null, errorCode = null) }
            val detailResult = getEventDetailUseCase(id)
            val myEventsResult = getMyRegisteredEventsUseCase()

            if (detailResult is NetworkResult.Success) {
                val isReg = if (myEventsResult is NetworkResult.Success) {
                    myEventsResult.data.any { it.id == id }
                } else {
                    false
                }
                setState { copy(isLoading = false, event = detailResult.data, isRegistered = isReg, error = null, errorCode = null) }
            } else if (detailResult is NetworkResult.Error) {
                setState { copy(isLoading = false, error = detailResult.message, errorCode = detailResult.code) }
            }
        }
    }

    private fun register() {
        val eventId = uiState.value.event?.id ?: return
        viewModelScope.launch {
            setState { copy(isRegistering = true) }
            when (val result = registerToEventUseCase(eventId)) {
                is NetworkResult.Success -> {
                    setState { copy(isRegistering = false, isRegistered = true) }
                    setEffect { EventDetailContract.Effect.ShowMessage("Berhasil mendaftar ke event!") }
                    loadDetail(eventId, forceRefresh = true)
                }
                is NetworkResult.Error -> {
                    setState { copy(isRegistering = false) }
                    setEffect { EventDetailContract.Effect.ShowMessage(result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    private fun unregister() {
        val eventId = uiState.value.event?.id ?: return
        viewModelScope.launch {
            setState { copy(isRegistering = true) }
            when (val result = unregisterFromEventUseCase(eventId)) {
                is NetworkResult.Success -> {
                    setState { copy(isRegistering = false, isRegistered = false) }
                    setEffect { EventDetailContract.Effect.ShowMessage("Pendaftaran dibatalkan.") }
                    loadDetail(eventId, forceRefresh = true)
                }
                is NetworkResult.Error -> {
                    setState { copy(isRegistering = false) }
                    setEffect { EventDetailContract.Effect.ShowMessage(result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
