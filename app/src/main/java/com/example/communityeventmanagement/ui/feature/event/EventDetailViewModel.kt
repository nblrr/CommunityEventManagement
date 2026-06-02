package com.example.communityeventmanagement.ui.feature.event

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.domain.usecase.community.GetCommunityDetail
import com.example.communityeventmanagement.domain.usecase.event.AddEventRating
import com.example.communityeventmanagement.domain.usecase.event.CancelEvent
import com.example.communityeventmanagement.domain.usecase.event.DeleteEvent
import com.example.communityeventmanagement.domain.usecase.event.GetEventDetail
import com.example.communityeventmanagement.domain.usecase.event.GetRegisteredEventIds
import com.example.communityeventmanagement.domain.usecase.event.JoinEvent
import com.example.communityeventmanagement.domain.usecase.user.GetCurrentUser
import com.example.communityeventmanagement.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getEventDetail: GetEventDetail,
    getCommunityDetailUseCase: GetCommunityDetail,
    private val getCurrentUser: GetCurrentUser,
    private val joinEvent: JoinEvent,
    private val cancelEvent: CancelEvent,
    getRegisteredEventIds: GetRegisteredEventIds,
    private val deleteEvent: DeleteEvent,
    private val addEventRating: AddEventRating
) : ViewModel() {

    private val eventId: Int = checkNotNull(savedStateHandle["eventId"])
    private val communityId: Int = checkNotNull(savedStateHandle["communityId"])

    val event = getEventDetail(eventId, communityId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val community = getCommunityDetailUseCase(communityId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun toggleRegistration() {
        val userId = getCurrentUser().value?.id ?: return
        val isRegistered = registeredEventIds.value.contains(eventId)
        viewModelScope.launch {
            if (isRegistered) {
                cancelEvent(eventId, communityId, userId)
            } else {
                joinEvent(eventId, communityId, userId)
            }
        }
    }

    val registeredEventIds = getRegisteredEventIds().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )

    fun deleteEvent(onSuccess: () -> Unit) {
        viewModelScope.launch {
            when (deleteEvent(communityId, eventId)) {
                is Resource.Success -> onSuccess()
                is Resource.Error -> { /* Handle error */ }
                is Resource.Loading -> {}
            }
        }
    }

    fun submitRating(score: Int, comment: String) {
        val user = getCurrentUser().value ?: return
        viewModelScope.launch {
            addEventRating(communityId, eventId, user.id, user.name, score, comment)
        }
    }
}


