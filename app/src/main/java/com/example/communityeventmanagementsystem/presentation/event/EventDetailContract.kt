package com.example.communityeventmanagementsystem.presentation.event

import com.example.communityeventmanagementsystem.domain.model.Event as DomainEvent

class EventDetailContract {
    data class State(
        val isLoading: Boolean = false,
        val event: DomainEvent? = null,
        val error: String? = null,
        val errorCode: Int? = null,
        val isRegistering: Boolean = false,
        val isRegistered: Boolean = false,
        val isCommunityMember: Boolean = false,
        val isSubmittingRating: Boolean = false,
        val hasRated: Boolean = false,
        val isOrganizer: Boolean = false,
        val participants: List<com.example.communityeventmanagementsystem.domain.model.User> = emptyList()
    )

    sealed class Event {
        data class LoadDetail(val id: Long) : Event()
        data object Register : Event()
        data object Unregister : Event()
        data object JoinCommunity : Event()
        data object Logout : Event()
        data class RateEvent(val rating: Int, val comment: String) : Event()
    }

    sealed class Effect {
        data class ShowMessage(val message: String) : Effect()
        data object NavigateToLogin : Effect()
    }
}
