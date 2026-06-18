package com.example.communityeventmanagementsystem.presentation.community

import androidx.paging.PagingData
import com.example.communityeventmanagementsystem.domain.model.Community
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class CommunityListContract {
    data class State(
        val categoryId: Long? = null,
        val searchQuery: String = "",
        val sortBy: String? = null,
        val communities: Flow<PagingData<Community>> = emptyFlow(),
        val categories: List<com.example.communityeventmanagementsystem.domain.model.Category> = emptyList(),
        val isInitialized: Boolean = false
    )

    sealed class Event {
        data class LoadCommunities(
            val categoryId: Long? = null,
            val sortBy: String? = null
        ) : Event()
        data class SearchCommunities(val query: String, val immediate: Boolean = false) : Event()
        data class OnCommunityClicked(val communityId: Long) : Event()
    }

    sealed class Effect {
        data class NavigateToCommunityDetail(val communityId: Long) : Effect()
    }
}
