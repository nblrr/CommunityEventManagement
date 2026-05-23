package com.example.communityeventmanagement.ui.feature.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.domain.usecase.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CommunityDetailViewModel(
    private val getCurrentUser: GetCurrentUser,
    private val getCommunityDetail: GetCommunityDetail,
    private val joinCommunity: JoinCommunity,
    getJoinedCommunityIds: GetJoinedCommunityIds,
    private val getRegisteredEventIds: GetRegisteredEventIds,
    private val deleteCommunity: DeleteCommunity
) : ViewModel() {

    fun getCommunity(id: Int) = getCommunityDetail(id)
    
    fun toggleJoin(communityId: Int) {
        val userId = getCurrentUser().value?.id ?: return
        viewModelScope.launch {
            joinCommunity(communityId, userId)
        }
    }
    
    val joinedCommunityIds = getJoinedCommunityIds().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )

    val registeredEventIds = getRegisteredEventIds().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )

    fun isJoined(communityId: Int) = joinedCommunityIds.value.contains(communityId)

    fun isEventRegistered(eventId: Int) = registeredEventIds.value.contains(eventId)

    fun deleteCommunity(communityId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = deleteCommunity(communityId)
            if (result.isSuccess) {
                onSuccess()
            }
        }
    }

    fun isOrganizer(organizerId: String): Boolean {
        return getCurrentUser().value?.id == organizerId
    }
}
