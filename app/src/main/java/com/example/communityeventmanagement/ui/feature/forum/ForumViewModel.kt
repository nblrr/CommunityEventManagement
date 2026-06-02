package com.example.communityeventmanagement.ui.feature.forum

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.domain.model.ForumMessage
import com.example.communityeventmanagement.domain.usecase.user.GetCurrentUser
import com.example.communityeventmanagement.domain.usecase.forum.GetForumMessages
import com.example.communityeventmanagement.domain.usecase.forum.SendMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ForumViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCurrentUser: GetCurrentUser,
    getForumMessages: GetForumMessages,
    private val sendMessage: SendMessage
) : ViewModel() {

    private val communityId: Int = savedStateHandle.get<Int>("communityId") ?: 0
    var messageText by mutableStateOf("")

    val messages: StateFlow<List<ForumMessage>> = getForumMessages(communityId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun sendMessage() {
        if (communityId == 0) return
        val user = getCurrentUser().value ?: return
        if (messageText.isBlank()) return

        val newMessage = ForumMessage(
            id = "msg_${System.currentTimeMillis()}",
            communityId = communityId,
            sender = user.name,
            message = messageText.trim(),
            time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
            avatarInitials = user.name.take(1).uppercase()
        )

        viewModelScope.launch {
            sendMessage(communityId, newMessage)
            messageText = ""
        }
    }
}


