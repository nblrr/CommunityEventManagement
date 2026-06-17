package com.example.communityeventmanagementsystem.presentation.event

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.usecase.event.GetEventDetailUseCase
import com.example.communityeventmanagementsystem.domain.usecase.event.GetMyRegisteredEventsUseCase
import com.example.communityeventmanagementsystem.domain.usecase.event.RegisterToEventUseCase
import com.example.communityeventmanagementsystem.domain.usecase.event.UnregisterFromEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val getEventDetailUseCase: GetEventDetailUseCase,
    private val registerToEventUseCase: RegisterToEventUseCase,
    private val unregisterFromEventUseCase: UnregisterFromEventUseCase,
    private val getMyRegisteredEventsUseCase: GetMyRegisteredEventsUseCase,
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
        }
    }

    private fun loadDetail(id: Long, forceRefresh: Boolean) {
        if (!forceRefresh && uiState.value.event?.id == id && !uiState.value.isLoading) return
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            val detailResult = getEventDetailUseCase(id)
            val myEventsResult = getMyRegisteredEventsUseCase()

            if (detailResult is NetworkResult.Success) {
                val isReg = if (myEventsResult is NetworkResult.Success) {
                    myEventsResult.data.any { it.id == id }
                } else {
                    false
                }
                setState { copy(isLoading = false, event = detailResult.data, isRegistered = isReg, error = null) }
            } else if (detailResult is NetworkResult.Error) {
                setState { copy(isLoading = false, error = detailResult.message) }
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
                    setEffect { EventDetailContract.Effect.ShowSuccessMessage }
                    loadDetail(eventId, forceRefresh = true)
                }
                is NetworkResult.Error -> setState { copy(isRegistering = false, error = result.message) }
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
                    setEffect { EventDetailContract.Effect.ShowSuccessMessage }
                    loadDetail(eventId, forceRefresh = true)
                }
                is NetworkResult.Error -> setState { copy(isRegistering = false, error = result.message) }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
