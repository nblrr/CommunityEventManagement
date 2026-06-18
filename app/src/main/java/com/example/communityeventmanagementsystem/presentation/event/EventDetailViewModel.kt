package com.example.communityeventmanagementsystem.presentation.event

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.usecase.event.GetEventDetailUseCase
import com.example.communityeventmanagementsystem.domain.usecase.event.GetMyRegisteredEventsUseCase
import com.example.communityeventmanagementsystem.domain.usecase.event.RegisterToEventUseCase
import com.example.communityeventmanagementsystem.domain.usecase.event.UnregisterFromEventUseCase
import com.example.communityeventmanagementsystem.domain.usecase.event.RateEventUseCase
import com.example.communityeventmanagementsystem.domain.usecase.event.GetEventParticipantsUseCase
import com.example.communityeventmanagementsystem.domain.usecase.community.JoinCommunityUseCase
import com.example.communityeventmanagementsystem.domain.usecase.home.GetMyCommunitiesUseCase
import com.example.communityeventmanagementsystem.core.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val getEventDetailUseCase: GetEventDetailUseCase,
    private val registerToEventUseCase: RegisterToEventUseCase,
    private val unregisterFromEventUseCase: UnregisterFromEventUseCase,
    private val getMyRegisteredEventsUseCase: GetMyRegisteredEventsUseCase,
    private val rateEventUseCase: RateEventUseCase,
    private val joinCommunityUseCase: JoinCommunityUseCase,
    private val getMyCommunitiesUseCase: GetMyCommunitiesUseCase,
    private val getEventParticipantsUseCase: GetEventParticipantsUseCase,
    private val sessionManager: SessionManager,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel<EventDetailContract.State, EventDetailContract.Event, EventDetailContract.Effect>() {

    private var currentUserId: Long = -1L

    override fun createInitialState(): EventDetailContract.State = EventDetailContract.State()

    init {
        viewModelScope.launch {
            sessionManager.userData.collect { json ->
                if (json != null) {
                    try {
                        val user = com.google.gson.Gson().fromJson(json, com.example.communityeventmanagementsystem.data.remote.dto.UserDto::class.java)
                        currentUserId = user.id
                        uiState.value.event?.let { event ->
                            checkOrganizerAndLoadParticipants(event)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        savedStateHandle.get<Long>("id")?.let { id ->
            loadDetail(id, forceRefresh = false)
        }
    }

    override fun handleEvent(event: EventDetailContract.Event) {
        when (event) {
            is EventDetailContract.Event.LoadDetail -> loadDetail(event.id, forceRefresh = false)
            is EventDetailContract.Event.Register -> register()
            is EventDetailContract.Event.Unregister -> unregister()
            is EventDetailContract.Event.JoinCommunity -> joinCommunity()
            is EventDetailContract.Event.Logout -> logout()
            is EventDetailContract.Event.RateEvent -> rate(event.rating, event.comment)
        }
    }

    private fun logout() {
        viewModelScope.launch {
            sessionManager.logout()
            setEffect { EventDetailContract.Effect.NavigateToLogin }
        }
    }

    private fun loadDetail(id: Long, forceRefresh: Boolean) {
        if (!forceRefresh && uiState.value.event?.id == id && !uiState.value.isLoading) return
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null, errorCode = null) }
            val detailResult = getEventDetailUseCase(id)
            val myEventsResult = getMyRegisteredEventsUseCase()
            val myCommResult = getMyCommunitiesUseCase()

            if (detailResult is NetworkResult.Success) {
                val isReg = if (myEventsResult is NetworkResult.Success) {
                    myEventsResult.data.any { it.id == id }
                } else {
                    false
                }
                val isMember = if (myCommResult is NetworkResult.Success) {
                    myCommResult.data.any { it.id == detailResult.data.communityId }
                } else {
                    false
                }
                setState { 
                    copy(
                        isLoading = false, 
                        event = detailResult.data, 
                        isRegistered = isReg, 
                        isCommunityMember = isMember, 
                        error = null, 
                        errorCode = null
                    ) 
                }
                checkOrganizerAndLoadParticipants(detailResult.data)
            } else if (detailResult is NetworkResult.Error) {
                setState { copy(isLoading = false, error = detailResult.message, errorCode = detailResult.code) }
            }
        }
    }

    private fun checkOrganizerAndLoadParticipants(event: com.example.communityeventmanagementsystem.domain.model.Event) {
        val isOrg = (currentUserId != -1L && event.organizerId == currentUserId)
        setState { copy(isOrganizer = isOrg) }
        if (isOrg) {
            viewModelScope.launch {
                when (val result = getEventParticipantsUseCase(event.id)) {
                    is NetworkResult.Success -> {
                        setState { copy(participants = result.data) }
                    }
                    else -> {
                        setState { copy(participants = emptyList()) }
                    }
                }
            }
        } else {
            setState { copy(participants = emptyList()) }
        }
    }

    private fun joinCommunity() {
        val communityId = uiState.value.event?.communityId ?: return
        val oldState = uiState.value
        viewModelScope.launch {
            setState { copy(isRegistering = true, isCommunityMember = true) }
            when (val result = joinCommunityUseCase(communityId)) {
                is NetworkResult.Success -> {
                    setState { copy(isRegistering = false) }
                    setEffect { EventDetailContract.Effect.ShowMessage("Berhasil bergabung dengan komunitas!") }
                }
                is NetworkResult.Error -> {
                    setState { copy(isRegistering = false, isCommunityMember = oldState.isCommunityMember) }
                    setEffect { EventDetailContract.Effect.ShowMessage(result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    private fun register() {
        val eventId = uiState.value.event?.id ?: return
        val oldState = uiState.value
        viewModelScope.launch {
            setState { 
                copy(
                    isRegistering = true, 
                    isRegistered = true,
                    event = event?.copy(attendeeCount = (event?.attendeeCount ?: 0) + 1)
                ) 
            }
            when (val result = registerToEventUseCase(eventId)) {
                is NetworkResult.Success -> {
                    setState { copy(isRegistering = false) }
                    setEffect { EventDetailContract.Effect.ShowMessage("Berhasil mendaftar ke event!") }
                }
                is NetworkResult.Error -> {
                    setState { 
                        copy(
                            isRegistering = false, 
                            isRegistered = oldState.isRegistered,
                            event = oldState.event
                        ) 
                    }
                    setEffect { EventDetailContract.Effect.ShowMessage(result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    private fun unregister() {
        val eventId = uiState.value.event?.id ?: return
        val oldState = uiState.value
        viewModelScope.launch {
            setState { 
                copy(
                    isRegistering = true, 
                    isRegistered = false,
                    event = event?.copy(attendeeCount = (event?.attendeeCount ?: 1).coerceAtLeast(1) - 1)
                ) 
            }
            when (val result = unregisterFromEventUseCase(eventId)) {
                is NetworkResult.Success -> {
                    setState { copy(isRegistering = false) }
                    setEffect { EventDetailContract.Effect.ShowMessage("Pendaftaran dibatalkan.") }
                }
                is NetworkResult.Error -> {
                    setState { 
                        copy(
                            isRegistering = false, 
                            isRegistered = oldState.isRegistered,
                            event = oldState.event
                        ) 
                    }
                    setEffect { EventDetailContract.Effect.ShowMessage(result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    private fun rate(rating: Int, comment: String) {
        val eventId = uiState.value.event?.id ?: return
        val oldState = uiState.value
        viewModelScope.launch {
            setState { copy(isSubmittingRating = true, hasRated = true) }
            when (val result = rateEventUseCase(eventId, rating, comment)) {
                is NetworkResult.Success -> {
                    setState { copy(isSubmittingRating = false) }
                    setEffect { EventDetailContract.Effect.ShowMessage("Terima kasih atas penilaian Anda!") }
                }
                is NetworkResult.Error -> {
                    setState { copy(isSubmittingRating = false, hasRated = oldState.hasRated) }
                    setEffect { EventDetailContract.Effect.ShowMessage(result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
