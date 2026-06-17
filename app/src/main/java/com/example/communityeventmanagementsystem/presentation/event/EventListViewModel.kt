package com.example.communityeventmanagementsystem.presentation.event

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.usecase.event.GetEventsUseCase
import com.example.communityeventmanagementsystem.domain.usecase.home.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventListViewModel @Inject constructor(
    private val getEventsUseCase: GetEventsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<EventListContract.State, EventListContract.Event, EventListContract.Effect>() {

    init {
        val catId = savedStateHandle.get<Long>("categoryId")
        val categoryId = if (catId != null && catId != -1L) catId else null
        loadCategories()
        loadEvents(categoryId, "")
    }

    override fun createInitialState(): EventListContract.State = EventListContract.State()

    override fun handleEvent(event: EventListContract.Event) {
        when (event) {
            is EventListContract.Event.LoadEvents -> {
                val cleanCategoryId = if (event.categoryId == -1L) null else event.categoryId
                loadEvents(cleanCategoryId, uiState.value.searchQuery)
            }
            is EventListContract.Event.SearchEvents -> loadEvents(uiState.value.categoryId, event.query)
            is EventListContract.Event.OnEventClicked -> setEffect { EventListContract.Effect.NavigateToEventDetail(event.eventId) }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            when (val result = getCategoriesUseCase()) {
                is NetworkResult.Success -> {
                    setState { copy(categories = result.data) }
                }
                else -> {}
            }
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
