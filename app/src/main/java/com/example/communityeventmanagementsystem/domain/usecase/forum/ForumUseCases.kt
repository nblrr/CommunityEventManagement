package com.example.communityeventmanagementsystem.domain.usecase.forum

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.domain.model.ForumMessage
import com.example.communityeventmanagementsystem.domain.repository.ForumRepository
import javax.inject.Inject

class GetForumMessagesUseCase @Inject constructor(private val repository: ForumRepository) {
    suspend operator fun invoke(communityId: Long): NetworkResult<List<ForumMessage>> =
        repository.getMessages(communityId)
}

class SendForumMessageUseCase @Inject constructor(private val repository: ForumRepository) {
    suspend operator fun invoke(communityId: Long, message: String): NetworkResult<ForumMessage> =
        repository.sendMessage(communityId, message)
}

class DeleteForumMessageUseCase @Inject constructor(private val repository: ForumRepository) {
    suspend operator fun invoke(messageId: Long): NetworkResult<Unit> =
        repository.deleteMessage(messageId)
}
