package com.example.communityeventmanagementsystem.presentation.forum

import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.session.SessionManager
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.usecase.forum.GetForumMessagesUseCase
import com.example.communityeventmanagementsystem.domain.usecase.forum.SendForumMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.example.communityeventmanagementsystem.domain.usecase.home.GetMyCommunitiesUseCase

import androidx.lifecycle.SavedStateHandle

@HiltViewModel
class ForumViewModel @Inject constructor(
    private val getForumMessagesUseCase: GetForumMessagesUseCase,
    private val sendForumMessageUseCase: SendForumMessageUseCase,
    private val sessionManager: SessionManager,
    private val getMyCommunitiesUseCase: GetMyCommunitiesUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<ForumContract.State, ForumContract.Event, ForumContract.Effect>() {

    private var pollingJob: Job? = null
    private var communityId: Long = -1L

    init {
        savedStateHandle.get<Long>("id")?.let { id ->
            communityId = id
            checkMembershipAndStartPolling()
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
                if (communityId == event.communityId && pollingJob?.isActive == true) return
                communityId = event.communityId
                checkMembershipAndStartPolling()
            }
            is ForumContract.Event.OnMessageChanged -> setState { copy(currentMessage = event.message) }
            is ForumContract.Event.OnSendClicked -> sendMessage()
            is ForumContract.Event.OnRefresh -> {
                viewModelScope.launch {
                    loadMessages()
                }
            }
        }
    }

    private fun checkMembershipAndStartPolling() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            when (val myCommResult = getMyCommunitiesUseCase()) {
                is NetworkResult.Success -> {
                    val isMember = myCommResult.data.any { it.id == communityId }
                    setState { copy(isMember = isMember) }
                    if (isMember) {
                        startPolling()
                    } else {
                        setState { copy(isLoading = false, messages = emptyList()) }
                    }
                }
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false, isMember = false, error = myCommResult.message) }
                }
                is NetworkResult.Loading -> {
                    setState { copy(isLoading = true) }
                }
            }
        }
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                loadMessages()
                delay(5000) // Poll every 5 seconds
            }
        }
    }

    private suspend fun loadMessages() {
        if (communityId == -1L) return
        when (val result = getForumMessagesUseCase(communityId)) {
            is NetworkResult.Success -> {
                if (result.data != uiState.value.messages) {
                    setState { copy(messages = result.data, isLoading = false) }
                    setEffect { ForumContract.Effect.ScrollToBottom }
                }
            }
            is NetworkResult.Error -> {
                if (uiState.value.messages.isEmpty()) {
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
                }
                is NetworkResult.Error -> {
                    setState { copy(error = result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
