package com.example.communityeventmanagementsystem.presentation.event

class CreateEventContract {
    data class State(
        val isLoading: Boolean = false,
        val error: String? = null,
        val categories: List<com.example.communityeventmanagementsystem.domain.model.Category> = emptyList(),
        val managedCommunities: List<com.example.communityeventmanagementsystem.domain.model.Community> = emptyList()
    )

    sealed class Event {
        data class CreateEvent(
            val communityId: Long,
            val categoryId: Long,
            val title: String,
            val description: String,
            val eventDate: String,
            val eventTime: String,
            val endTime: String?,
            val location: String,
            val maxAttendees: Int,
            val isOnline: Boolean,
            val coverImageUri: android.net.Uri?
        ) : Event()
    }

    sealed class Effect {
        object NavigateBack : Effect()
        data class ShowMessage(val message: String) : Effect()
    }
}
