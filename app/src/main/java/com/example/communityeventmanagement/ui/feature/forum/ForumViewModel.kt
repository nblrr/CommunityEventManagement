package com.example.communityeventmanagement.ui.feature.forum

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.domain.entities.ForumMessage
import com.example.communityeventmanagement.domain.usecase.GetCommunityDetail
import com.example.communityeventmanagement.domain.usecase.GetCurrentUser
import com.example.communityeventmanagement.domain.usecase.GetForumMessages
import com.example.communityeventmanagement.domain.usecase.SendMessage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ForumViewModel(
    private val getCurrentUser: GetCurrentUser,
    private val getCommunityDetail: GetCommunityDetail,
    private val getForumMessages: GetForumMessages,
    private val sendMessage: SendMessage
) : ViewModel() {

    var messageText by mutableStateOf("")

    fun getCommunity(id: Int) = getCommunityDetail(id)

    fun getMessages(communityId: Int) = getForumMessages(communityId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun sendMessage(communityId: Int) {
        val user = getCurrentUser().value ?: return
        if (messageText.isBlank()) return

        val newMessage = ForumMessage(
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
