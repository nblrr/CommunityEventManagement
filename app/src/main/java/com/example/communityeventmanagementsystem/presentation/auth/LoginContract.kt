package com.example.communityeventmanagementsystem.presentation.auth

import com.example.communityeventmanagementsystem.domain.model.User

class LoginContract {
    data class State(
        val isLoading: Boolean = false,
        val user: User? = null,
        val error: String? = null
    )

    sealed class Event {
        data class OnLoginClicked(val email: String, val pass: String) : Event()
    }

    sealed class Effect {
        object NavigationToHome : Effect()
    }
}
