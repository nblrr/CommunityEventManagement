package com.example.communityeventmanagementsystem.presentation.home

import com.example.communityeventmanagementsystem.domain.model.Category
import com.example.communityeventmanagementsystem.domain.model.Community
import com.example.communityeventmanagementsystem.domain.model.Event as DomainEvent

class HomeContract {
    data class State(
        val isLoading: Boolean = false,
        val userName: String? = null,
        val categories: List<Category> = emptyList(),
        val upcomingEvents: List<DomainEvent> = emptyList(),
        val recommendedEvents: List<DomainEvent> = emptyList(),
        val myCommunities: List<Community> = emptyList(),
        val error: String? = null
    )

    sealed class Event {
        object LoadHomeData : Event()
        object RefreshHomeData : Event()
        data class OnCategoryClicked(val categoryId: Long) : Event()
        data class OnEventClicked(val eventId: Long) : Event()
        data class OnCommunityClicked(val communityId: Long) : Event()
        object OnProfileClicked : Event()
        object OnNotificationClicked : Event()
    }

    sealed class Effect {
        data class NavigateToCategory(val categoryId: Long) : Effect()
        data class NavigateToEventDetail(val eventId: Long) : Effect()
        data class NavigateToCommunityDetail(val communityId: Long) : Effect()
        object NavigateToProfile : Effect()
        object NavigateToNotifications : Effect()
    }
}
