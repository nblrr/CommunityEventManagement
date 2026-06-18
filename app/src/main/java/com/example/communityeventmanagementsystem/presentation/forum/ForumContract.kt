package com.example.communityeventmanagementsystem.presentation.forum

import com.example.communityeventmanagementsystem.domain.model.ForumMessage

class ForumContract {
    data class State(
        val isLoading: Boolean = false,
        val isMember: Boolean = true,
        val messages: List<ForumMessage> = emptyList(),
        val error: String? = null,
        val currentMessage: String = "",
        val currentUserId: Long = -1L
    )

    sealed class Event {
        data class LoadMessages(val communityId: Long) : Event()
        data class OnMessageChanged(val message: String) : Event()
        object OnSendClicked : Event()
        object OnRefresh : Event()
        data class DeleteMessage(val messageId: Long) : Event()
    }

    sealed class Effect {
        object ScrollToBottom : Effect()
        data class ShowMessage(val message: String) : Effect()
    }
}
