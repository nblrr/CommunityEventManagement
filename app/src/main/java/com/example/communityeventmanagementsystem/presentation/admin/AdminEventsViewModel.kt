package com.example.communityeventmanagementsystem.presentation.admin

import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.usecase.admin.DeleteEventUseCase
import com.example.communityeventmanagementsystem.domain.usecase.admin.GetAdminEventsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class AdminEventsContract {
    data class State(
        val isLoading: Boolean = false,
        val searchQuery: String = "",
        val events: List<com.example.communityeventmanagementsystem.domain.model.Event> = emptyList(),
        val error: String? = null
    )

    sealed class Event {
        object LoadEvents : Event()
        data class OnSearchQueryChanged(val query: String) : Event()
        data class OnDeleteEvent(val id: Long) : Event()
    }

    sealed class Effect {
        data class ShowSnackbar(val message: String) : Effect()
    }
}

@HiltViewModel
class AdminEventsViewModel @Inject constructor(
    private val getEventsUseCase: GetAdminEventsUseCase,
    private val deleteEventUseCase: DeleteEventUseCase
) : BaseViewModel<AdminEventsContract.State, AdminEventsContract.Event, AdminEventsContract.Effect>() {

    override fun createInitialState(): AdminEventsContract.State = AdminEventsContract.State()

    override fun handleEvent(event: AdminEventsContract.Event) {
        when (event) {
            is AdminEventsContract.Event.LoadEvents -> loadEvents()
            is AdminEventsContract.Event.OnSearchQueryChanged -> {
                setState { copy(searchQuery = event.query) }
                loadEvents()
            }
            is AdminEventsContract.Event.OnDeleteEvent -> deleteEvent(event.id)
        }
    }

    private fun loadEvents() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            val query = if (uiState.value.searchQuery.isBlank()) null else uiState.value.searchQuery
            when (val result = getEventsUseCase(query)) {
                is NetworkResult.Success -> {
                    setState { copy(isLoading = false, events = result.data) }
                }
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false, error = result.message) }
                }
                else -> {}
            }
        }
    }

    private fun deleteEvent(id: Long) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            when (val result = deleteEventUseCase(id)) {
                is NetworkResult.Success -> {
                    setState { copy(isLoading = false) }
                    setEffect { AdminEventsContract.Effect.ShowSnackbar("Event deleted successfully.") }
                    loadEvents()
                }
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false) }
                    setEffect { AdminEventsContract.Effect.ShowSnackbar("Failed to delete event: ${result.message}") }
                }
                else -> {}
            }
        }
    }
}
