package com.example.communityeventmanagementsystem.presentation.trusted

import com.example.communityeventmanagementsystem.domain.model.TrustedApplication

class TrustedAppContract {
    data class State(
        val isLoading: Boolean = false,
        val application: TrustedApplication? = null,
        val error: String? = null,
        val isSubmitting: Boolean = false,
        val communityName: String = "",
        val reason: String = "",
        val experience: String = ""
    )

    sealed class Event {
        object LoadMyApplication : Event()
        data class OnCommunityNameChanged(val name: String) : Event()
        data class OnReasonChanged(val reason: String) : Event()
        data class OnExperienceChanged(val experience: String) : Event()
        object OnSubmitClicked : Event()
    }

    sealed class Effect {
        object ShowSuccessMessage : Effect()
    }
}
