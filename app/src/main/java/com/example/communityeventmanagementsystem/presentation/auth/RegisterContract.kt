package com.example.communityeventmanagementsystem.presentation.auth

import com.example.communityeventmanagementsystem.domain.model.User

class RegisterContract {
    data class State(
        val isLoading: Boolean = false,
        val user: User? = null,
        val error: String? = null
    )

    sealed class Event {
        data class OnRegisterClicked(
            val name: String,
            val email: String,
            val pass: String,
            val passConfirm: String
        ) : Event()
    }

    sealed class Effect {
        object NavigationToHome : Effect()
    }
}
