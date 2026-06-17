package com.example.communityeventmanagementsystem.presentation.community

import com.example.communityeventmanagementsystem.domain.model.Community

class CommunityDetailContract {
    data class State(
        val isLoading: Boolean = false,
        val community: Community? = null,
        val error: String? = null,
        val isJoining: Boolean = false,
        val isJoined: Boolean = false
    )

    sealed class Event {
        data class LoadDetail(val id: Long) : Event()
        object JoinCommunity : Event()
        object LeaveCommunity : Event()
        data class ShowErrorMessage(val message: String) : Event()
    }

    sealed class Effect {
        object ShowSuccessMessage : Effect()
    }
}
