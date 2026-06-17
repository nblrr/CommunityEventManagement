package com.example.communityeventmanagementsystem.presentation.event

import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.usecase.event.GetMyRegisteredEventsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedEventsViewModel @Inject constructor(
    private val getMyRegisteredEventsUseCase: GetMyRegisteredEventsUseCase
) : BaseViewModel<SavedEventsContract.State, SavedEventsContract.Event, SavedEventsContract.Effect>() {

    override fun createInitialState(): SavedEventsContract.State = SavedEventsContract.State()

    override fun handleEvent(event: SavedEventsContract.Event) {
        when (event) {
            is SavedEventsContract.Event.LoadSavedEvents -> loadSavedEvents()
            is SavedEventsContract.Event.OnEventClicked -> setEffect { SavedEventsContract.Effect.NavigateToEventDetail(event.eventId) }
        }
    }

    private fun loadSavedEvents() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            when (val result = getMyRegisteredEventsUseCase()) {
                is NetworkResult.Success -> {
                    setState { copy(isLoading = false, savedEvents = result.data, error = null) }
                }
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false, error = result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
