package com.example.communityeventmanagementsystem.presentation.organizer

import com.example.communityeventmanagementsystem.domain.model.Community
import com.example.communityeventmanagementsystem.domain.model.Event as DomainEvent

class OrganizerContract {
    data class State(
        val isLoading: Boolean = false,
        val communities: List<Community> = emptyList(),
        val events: List<DomainEvent> = emptyList(),
        val error: String? = null
    )

    sealed class Event {
        object LoadDashboard : Event()
        data class OnDeleteEvent(val id: Long) : Event()
    }

    sealed class Effect {
        object ShowDeleteSuccess : Effect()
    }
}
