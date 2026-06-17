package com.example.communityeventmanagementsystem.presentation.profile

import com.example.communityeventmanagementsystem.domain.model.User
import java.io.File

class ProfileContract {
    data class State(
        val isLoading: Boolean = false,
        val user: User? = null,
        val error: String? = null,
        val errorCode: Int? = null,
        val isSessionExpired: Boolean = false
    )

    sealed class Event {
        object LoadProfile : Event()
        data class UpdateProfile(val user: User) : Event()
        data class UploadAvatar(val file: File) : Event()
        object BecomeOrganizer : Event()
        object Logout : Event()
    }

    sealed class Effect {
        object NavigateToLogin : Effect()
        object ProfileUpdated : Effect()
    }
}
