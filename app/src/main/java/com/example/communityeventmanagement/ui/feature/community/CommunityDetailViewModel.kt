package com.example.communityeventmanagement.ui.feature.community

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.domain.usecase.DeleteCommunity
import com.example.communityeventmanagement.domain.usecase.GetCommunityDetail
import com.example.communityeventmanagement.domain.usecase.GetCurrentUser
import com.example.communityeventmanagement.domain.usecase.GetJoinedCommunityIds
import com.example.communityeventmanagement.domain.usecase.GetRegisteredEventIds
import com.example.communityeventmanagement.domain.usecase.JoinCommunity
import com.example.communityeventmanagement.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getCurrentUser: GetCurrentUser,
    private val getCommunityDetail: GetCommunityDetail,
    private val joinCommunity: JoinCommunity,
    getJoinedCommunityIds: GetJoinedCommunityIds,
    private val getRegisteredEventIds: GetRegisteredEventIds,
    private val deleteCommunity: DeleteCommunity
) : ViewModel() {

    private val communityId: Int = checkNotNull(savedStateHandle["communityId"])

    val community = getCommunityDetail(communityId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
    
    fun toggleJoin() {
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

    fun deleteCommunity(onSuccess: () -> Unit) {
        viewModelScope.launch {
            when (deleteCommunity(communityId)) {
                is Resource.Success -> onSuccess()
                is Resource.Error -> { /* Handle error */ }
                is Resource.Loading -> {}
            }
        }
    }

    fun isOrganizer(organizerId: String): Boolean {
        return getCurrentUser().value?.id == organizerId
    }
}
