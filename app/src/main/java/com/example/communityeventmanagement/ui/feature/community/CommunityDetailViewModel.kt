package com.example.communityeventmanagement.ui.feature.community

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.domain.usecase.community.DeleteCommunity
import com.example.communityeventmanagement.domain.usecase.community.GetCommunityDetail
import com.example.communityeventmanagement.domain.usecase.community.GetJoinedCommunityIds
import com.example.communityeventmanagement.domain.usecase.community.JoinCommunity
import com.example.communityeventmanagement.domain.usecase.event.GetRegisteredEventIds
import com.example.communityeventmanagement.domain.usecase.user.GetCurrentUser
import com.example.communityeventmanagement.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCurrentUser: GetCurrentUser,
    getCommunityDetail: GetCommunityDetail,
    private val joinCommunity: JoinCommunity,
    getJoinedCommunityIds: GetJoinedCommunityIds,
    getRegisteredEventIds: GetRegisteredEventIds,
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
}


