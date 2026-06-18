package com.example.communityeventmanagementsystem.presentation.forum

import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.session.SessionManager
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.usecase.forum.GetForumMessagesUseCase
import com.example.communityeventmanagementsystem.domain.usecase.forum.SendForumMessageUseCase
import com.example.communityeventmanagementsystem.domain.usecase.forum.DeleteForumMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.example.communityeventmanagementsystem.domain.usecase.home.GetMyCommunitiesUseCase
import androidx.lifecycle.SavedStateHandle

@HiltViewModel
class ForumViewModel @Inject constructor(
    private val getForumMessagesUseCase: GetForumMessagesUseCase,
    private val sendForumMessageUseCase: SendForumMessageUseCase,
    private val deleteForumMessageUseCase: DeleteForumMessageUseCase,
    private val sessionManager: SessionManager,
    private val getMyCommunitiesUseCase: GetMyCommunitiesUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<ForumContract.State, ForumContract.Event, ForumContract.Effect>() {

    private var communityId: Long = -1L

    init {
        savedStateHandle.get<Long>("id")?.let { id ->
            communityId = id
            checkMembershipAndLoad()
        }
        viewModelScope.launch {
            sessionManager.userData.collect { json ->
                if (json != null) {
                    try {
                        val user = com.google.gson.Gson().fromJson(json, com.example.communityeventmanagementsystem.data.remote.dto.UserDto::class.java)
                        setState { copy(currentUserId = user.id) }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun createInitialState(): ForumContract.State = ForumContract.State()

    override fun handleEvent(event: ForumContract.Event) {
        when (event) {
            is ForumContract.Event.LoadMessages -> {
                communityId = event.communityId
                checkMembershipAndLoad()
            }
            is ForumContract.Event.OnMessageChanged -> setState { copy(currentMessage = event.message) }
            is ForumContract.Event.OnSendClicked -> sendMessage()
            is ForumContract.Event.OnRefresh -> {
                viewModelScope.launch {
                    loadMessages()
                }
            }
            is ForumContract.Event.DeleteMessage -> deleteMessage(event.messageId)
        }
    }

    private fun checkMembershipAndLoad() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            if (communityId == -1L) {
                setState { copy(isLoading = false) }
                return@launch
            }
            when (val result = getForumMessagesUseCase(communityId)) {
                is NetworkResult.Success -> {
                    val orderedMessages = result.data.reversed()
                    setState { 
                        copy(
                            isMember = true,
                            messages = orderedMessages,
                            isLoading = false
                        )
                    }
                    setEffect { ForumContract.Effect.ScrollToBottom }
                }
                is NetworkResult.Error -> {
                    if (result.code == 403) {
                        setState { copy(isLoading = false, isMember = false, messages = emptyList()) }
                    } else {
                        setState { copy(isLoading = false, error = result.message) }
                    }
                }
                is NetworkResult.Loading -> {
                    setState { copy(isLoading = true) }
                }
            }
        }
    }

    private suspend fun loadMessages() {
        if (communityId == -1L) return
        when (val result = getForumMessagesUseCase(communityId)) {
            is NetworkResult.Success -> {
                // Backend returns latest messages first (desc), we want to show oldest to newest for chat
                val orderedMessages = result.data.reversed()
                if (orderedMessages != uiState.value.messages) {
                    setState { copy(messages = orderedMessages, isLoading = false) }
                    setEffect { ForumContract.Effect.ScrollToBottom }
                }
            }
            is NetworkResult.Error -> {
                if (result.code == 403) {
                    setState { copy(isMember = false, messages = emptyList()) }
                } else if (uiState.value.messages.isEmpty()) {
                    setState { copy(error = result.message, isLoading = false) }
                }
            }
            is NetworkResult.Loading -> {
                if (uiState.value.messages.isEmpty()) {
                    setState { copy(isLoading = true) }
                }
            }
        }
    }

    private fun sendMessage() {
        val message = uiState.value.currentMessage
        if (message.isBlank() || communityId == -1L) return

        viewModelScope.launch {
            setState { copy(currentMessage = "") }
            when (val result = sendForumMessageUseCase(communityId, message)) {
                is NetworkResult.Success -> {
                    loadMessages()
                    setEffect { ForumContract.Effect.ShowMessage("Postingan berhasil dibuat") }
                }
                is NetworkResult.Error -> {
                    // Restore message on failure so the user doesn't lose their typing
                    setState { copy(currentMessage = message, error = result.message) }
                    setEffect { ForumContract.Effect.ShowMessage(result.message ?: "Gagal mengirim pesan") }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    private fun deleteMessage(messageId: Long) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            when (val result = deleteForumMessageUseCase(messageId)) {
                is NetworkResult.Success -> {
                    loadMessages()
                    setEffect { ForumContract.Effect.ShowMessage("Postingan berhasil dihapus") }
                }
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false, error = result.message) }
                    setEffect { ForumContract.Effect.ShowMessage(result.message ?: "Gagal menghapus pesan") }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
