package com.example.communityeventmanagement.ui.feature.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.domain.usecase.AddEventRating
import com.example.communityeventmanagement.domain.usecase.CancelEvent
import com.example.communityeventmanagement.domain.usecase.DeleteEvent
import com.example.communityeventmanagement.domain.usecase.GetCommunityDetail
import com.example.communityeventmanagement.domain.usecase.GetCurrentUser
import com.example.communityeventmanagement.domain.usecase.GetEventDetail
import com.example.communityeventmanagement.domain.usecase.GetRegisteredEventIds
import com.example.communityeventmanagement.domain.usecase.JoinEvent
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EventDetailViewModel(
    private val getEventDetail: GetEventDetail,
    private val getCommunityDetailUseCase: GetCommunityDetail,
    private val getCurrentUser: GetCurrentUser,
    private val joinEvent: JoinEvent,
    private val cancelEvent: CancelEvent,
    getRegisteredEventIds: GetRegisteredEventIds,
    private val deleteEvent: DeleteEvent,
    private val addEventRating: AddEventRating
) : ViewModel() {

    fun getEvent(eventId: Int, communityId: Int) = getEventDetail(eventId, communityId)
    fun getCommunityDetail(communityId: Int) = getCommunityDetailUseCase(communityId)

    fun toggleRegistration(eventId: Int, communityId: Int) {
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

    fun deleteEvent(communityId: Int, eventId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = deleteEvent(communityId, eventId)
            if (result.isSuccess) {
                onSuccess()
            }
        }
    }

    fun submitRating(communityId: Int, eventId: Int, score: Int, comment: String) {
        val user = getCurrentUser().value ?: return
        viewModelScope.launch {
            addEventRating(communityId, eventId, user.id, user.name, score, comment)
        }
    }
}
