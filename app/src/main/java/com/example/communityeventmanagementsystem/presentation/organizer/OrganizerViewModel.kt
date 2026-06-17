package com.example.communityeventmanagementsystem.presentation.organizer

import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.usecase.organizer.DeleteEventUseCase
import com.example.communityeventmanagementsystem.domain.usecase.organizer.GetMyManagedCommunitiesUseCase
import com.example.communityeventmanagementsystem.domain.usecase.organizer.GetMyManagedEventsUseCase
import com.example.communityeventmanagementsystem.domain.usecase.organizer.CreateCommunityUseCase
import com.example.communityeventmanagementsystem.domain.usecase.organizer.CreateEventUseCase
import com.example.communityeventmanagementsystem.domain.model.Community
import com.example.communityeventmanagementsystem.domain.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrganizerViewModel @Inject constructor(
    private val getMyManagedCommunitiesUseCase: GetMyManagedCommunitiesUseCase,
    private val getMyManagedEventsUseCase: GetMyManagedEventsUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
    private val createCommunityUseCase: CreateCommunityUseCase,
    private val createEventUseCase: CreateEventUseCase
) : BaseViewModel<OrganizerContract.State, OrganizerContract.Event, OrganizerContract.Effect>() {

    override fun createInitialState(): OrganizerContract.State = OrganizerContract.State()

    override fun handleEvent(event: OrganizerContract.Event) {
        when (event) {
            is OrganizerContract.Event.LoadDashboard -> loadDashboard(forceRefresh = false)
            is OrganizerContract.Event.OnDeleteEvent -> deleteEvent(event.id)
            is OrganizerContract.Event.CreateCommunity -> createCommunity(event.name, event.description, event.categoryId, event.coverImageUrl)
            is OrganizerContract.Event.CreateEvent -> createEvent(
                event.communityId, event.categoryId, event.title, event.description,
                event.eventDate, event.eventTime, event.location, event.maxAttendees, event.isOnline, event.coverImageUrl
            )
        }
    }

    private fun loadDashboard(forceRefresh: Boolean) {
        if (!forceRefresh && uiState.value.communities.isNotEmpty() && !uiState.value.isLoading) return
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            val communitiesDeferred = async { getMyManagedCommunitiesUseCase() }
            val eventsDeferred = async { getMyManagedEventsUseCase() }

            val communitiesResult = communitiesDeferred.await()
            val eventsResult = eventsDeferred.await()

            setState {
                copy(
                    isLoading = false,
                    communities = if (communitiesResult is NetworkResult.Success) communitiesResult.data else communities,
                    events = if (eventsResult is NetworkResult.Success) eventsResult.data else events,
                    error = (communitiesResult as? NetworkResult.Error)?.message
                        ?: (eventsResult as? NetworkResult.Error)?.message
                )
            }
        }
    }

    private fun deleteEvent(id: Long) {
        viewModelScope.launch {
            when (deleteEventUseCase(id)) {
                is NetworkResult.Success -> {
                    setEffect { OrganizerContract.Effect.ShowDeleteSuccess }
                    loadDashboard(forceRefresh = true)
                }
                else -> {}
            }
        }
    }

    private fun createCommunity(name: String, description: String, categoryId: Long, coverImageUrl: String?) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            val community = Community(
                id = 0L,
                name = name,
                description = description,
                memberCount = 0,
                categoryId = categoryId,
                coverImageUrl = coverImageUrl
            )
            when (val result = createCommunityUseCase(community)) {
                is NetworkResult.Success -> {
                    setEffect { OrganizerContract.Effect.ShowCreateCommunitySuccess }
                    loadDashboard(forceRefresh = true)
                }
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false, error = result.message) }
                }
                is NetworkResult.Loading -> {
                    setState { copy(isLoading = true) }
                }
            }
        }
    }

    private fun createEvent(
        communityId: Long,
        categoryId: Long,
        title: String,
        description: String,
        eventDate: String,
        eventTime: String,
        location: String,
        maxAttendees: Int,
        isOnline: Boolean,
        coverImageUrl: String?
    ) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            val event = Event(
                id = 0L,
                title = title,
                description = description,
                eventDate = eventDate,
                attendeeCount = 0,
                maxAttendees = maxAttendees,
                status = "ACTIVE",
                coverImageUrl = coverImageUrl,
                communityId = communityId,
                categoryId = categoryId,
                eventTime = eventTime,
                location = location,
                isOnline = isOnline
            )
            when (val result = createEventUseCase(event)) {
                is NetworkResult.Success -> {
                    setEffect { OrganizerContract.Effect.ShowCreateEventSuccess }
                    loadDashboard(forceRefresh = true)
                }
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false, error = result.message) }
                }
                is NetworkResult.Loading -> {
                    setState { copy(isLoading = true) }
                }
            }
        }
    }
}
