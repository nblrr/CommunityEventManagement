package com.example.communityeventmanagementsystem.presentation.event

import androidx.paging.PagingData
import com.example.communityeventmanagementsystem.domain.model.Event as DomainEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class EventListContract {
    data class State(
        val categoryId: Long? = null,
        val searchQuery: String = "",
        val status: String? = null,
        val sortBy: String? = null,
        val events: Flow<PagingData<DomainEvent>> = emptyFlow(),
        val categories: List<com.example.communityeventmanagementsystem.domain.model.Category> = emptyList(),
        val isInitialized: Boolean = false
    )

    sealed class Event {
        data class LoadEvents(
            val categoryId: Long? = null,
            val status: String? = null,
            val sortBy: String? = null
        ) : Event()
        data class SearchEvents(val query: String) : Event()
        data class OnEventClicked(val eventId: Long) : Event()
    }

    sealed class Effect {
        data class NavigateToEventDetail(val eventId: Long) : Effect()
    }
}
