package com.example.communityeventmanagement.ui.feature.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.domain.usecase.GetCommunities
import com.example.communityeventmanagement.domain.usecase.GetJoinedCommunityIds
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class CommunityListViewModel(
    getCommunities: GetCommunities,
    getJoinedCommunityIds: GetJoinedCommunityIds
) : ViewModel() {
    val communities = getCommunities().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    val joinedCommunityIds = getJoinedCommunityIds().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )
}
