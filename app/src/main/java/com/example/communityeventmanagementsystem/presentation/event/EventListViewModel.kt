package com.example.communityeventmanagementsystem.presentation.event

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.usecase.event.GetEventsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EventListViewModel @Inject constructor(
    private val getEventsUseCase: GetEventsUseCase
) : BaseViewModel<EventListContract.State, EventListContract.Event, EventListContract.Effect>() {

    override fun createInitialState(): EventListContract.State = EventListContract.State()

    override fun handleEvent(event: EventListContract.Event) {
        when (event) {
            is EventListContract.Event.LoadEvents -> loadEvents(event.categoryId, uiState.value.searchQuery)
            is EventListContract.Event.SearchEvents -> loadEvents(uiState.value.categoryId, event.query)
            is EventListContract.Event.OnEventClicked -> setEffect { EventListContract.Effect.NavigateToEventDetail(event.eventId) }
        }
    }

    private fun loadEvents(categoryId: Long?, query: String) {
        if (uiState.value.isInitialized && uiState.value.categoryId == categoryId && uiState.value.searchQuery == query) {
            return
        }
        val eventsFlow = getEventsUseCase(categoryId, query)
            .cachedIn(viewModelScope)
        
        setState { copy(categoryId = categoryId, searchQuery = query, events = eventsFlow, isInitialized = true) }
    }
}
