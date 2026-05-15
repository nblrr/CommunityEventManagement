package com.example.communityeventmanagement.features.forum

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.data.model.ForumMessage
import com.example.communityeventmanagement.data.repository.CommunityRepository
import com.example.communityeventmanagement.data.repository.UserRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ForumViewModel(
    private val userRepository: UserRepository,
    private val communityRepository: CommunityRepository
) : ViewModel() {
    var messageText by mutableStateOf("")

    fun getMessages(communityId: Int): List<ForumMessage> {
        return communityRepository.communities.find { it.id == communityId }?.forumMessages ?: emptyList()
    }

    fun sendMessage(communityId: Int) {
        if (messageText.isBlank()) return
        val user = userRepository.currentUser ?: return
        val newMessage = ForumMessage(
            sender = user.name,
            message = messageText.trim(),
            time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
            avatarInitials = user.name.take(1).uppercase()
        )
        
        val index = communityRepository.communities.indexOfFirst { it.id == communityId }
        if (index != -1) {
            val community = communityRepository.communities[index]
            communityRepository.communities[index] = community.copy(
                forumMessages = community.forumMessages + newMessage
            )
            viewModelScope.launch {
                communityRepository.saveForumData(communityId)
            }
            messageText = ""
        }
    }
}
