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
        data class CreateCommunity(
            val name: String,
            val description: String,
            val categoryId: Long,
            val coverImageUrl: String?
        ) : Event()
        data class CreateEvent(
            val communityId: Long,
            val categoryId: Long,
            val title: String,
            val description: String,
            val eventDate: String,
            val eventTime: String,
            val location: String,
            val maxAttendees: Int,
            val isOnline: Boolean,
            val coverImageUrl: String?
        ) : Event()
    }

    sealed class Effect {
        object ShowDeleteSuccess : Effect()
        object ShowCreateCommunitySuccess : Effect()
        object ShowCreateEventSuccess : Effect()
    }
}
