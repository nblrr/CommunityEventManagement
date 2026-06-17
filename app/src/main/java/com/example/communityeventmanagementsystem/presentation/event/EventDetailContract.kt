package com.example.communityeventmanagementsystem.presentation.event

import com.example.communityeventmanagementsystem.domain.model.Event as DomainEvent

class EventDetailContract {
    data class State(
        val isLoading: Boolean = false,
        val event: DomainEvent? = null,
        val error: String? = null,
        val isRegistering: Boolean = false,
        val isRegistered: Boolean = false
    )

    sealed class Event {
        data class LoadDetail(val id: Long) : Event()
        object Register : Event()
        object Unregister : Event()
    }

    sealed class Effect {
        object ShowSuccessMessage : Effect()
    }
}
