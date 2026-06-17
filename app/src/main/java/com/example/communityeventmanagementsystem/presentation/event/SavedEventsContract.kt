package com.example.communityeventmanagementsystem.presentation.event

import com.example.communityeventmanagementsystem.domain.model.Event as DomainEvent

class SavedEventsContract {
    data class State(
        val isLoading: Boolean = false,
        val savedEvents: List<DomainEvent> = emptyList(),
        val error: String? = null
    )

    sealed class Event {
        object LoadSavedEvents : Event()
        data class OnEventClicked(val eventId: Long) : Event()
    }

    sealed class Effect {
        data class NavigateToEventDetail(val eventId: Long) : Effect()
    }
}
