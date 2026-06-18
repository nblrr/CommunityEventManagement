package com.example.communityeventmanagementsystem.presentation.event

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.usecase.event.GetEventsUseCase
import com.example.communityeventmanagementsystem.domain.usecase.home.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventListViewModel @Inject constructor(
    private val getEventsUseCase: GetEventsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<EventListContract.State, EventListContract.Event, EventListContract.Effect>() {

    private var searchJob: kotlinx.coroutines.Job? = null

    init {
        val catId = savedStateHandle.get<Long>("categoryId")
        val categoryId = if (catId != null && catId != -1L) catId else null
        loadCategories()
        loadEvents(categoryId, "", null, null)
    }

    override fun createInitialState(): EventListContract.State = EventListContract.State()

    override fun handleEvent(event: EventListContract.Event) {
        when (event) {
            is EventListContract.Event.LoadEvents -> {
                val cleanCategoryId = if (event.categoryId == -1L) null else event.categoryId
                loadEvents(cleanCategoryId, uiState.value.searchQuery, event.status, event.sortBy)
            }
            is EventListContract.Event.SearchEvents -> {
                searchJob?.cancel()
                setState { copy(searchQuery = event.query) }
                searchJob = viewModelScope.launch {
                    delay(500)
                    loadEvents(uiState.value.categoryId, event.query, uiState.value.status, uiState.value.sortBy)
                }
            }
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

    private fun loadEvents(categoryId: Long?, query: String, status: String?, sortBy: String?) {
        if (uiState.value.isInitialized && 
            uiState.value.categoryId == categoryId && 
            uiState.value.searchQuery == query &&
            uiState.value.status == status &&
            uiState.value.sortBy == sortBy) {
            return
        }
        val eventsFlow = getEventsUseCase(categoryId, query, status, sortBy)
            .cachedIn(viewModelScope)
        
        setState { 
            copy(
                categoryId = categoryId, 
                searchQuery = query, 
                status = status, 
                sortBy = sortBy, 
                events = eventsFlow, 
                isInitialized = true
            ) 
        }
    }
}
