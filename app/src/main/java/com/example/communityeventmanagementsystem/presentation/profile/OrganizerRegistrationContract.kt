package com.example.communityeventmanagementsystem.presentation.profile

import com.example.communityeventmanagementsystem.domain.model.User

class OrganizerRegistrationContract {
    data class State(
        val isLoading: Boolean = false,
        val user: User? = null,
        val error: String? = null,
        val isSuccess: Boolean = false
    )

    sealed class Event {
        object OnSubmitClicked : Event()
    }

    sealed class Effect {
        object NavigateBack : Effect()
        object NavigateToOrganizerDashboard : Effect()
    }
}
