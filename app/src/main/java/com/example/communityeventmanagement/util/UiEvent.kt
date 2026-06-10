package com.example.communityeventmanagement.util

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
}
