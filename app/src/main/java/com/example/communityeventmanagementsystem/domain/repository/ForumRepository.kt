package com.example.communityeventmanagementsystem.domain.repository

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.domain.model.ForumMessage

interface ForumRepository {
    suspend fun getMessages(communityId: Long): NetworkResult<List<ForumMessage>>
    suspend fun sendMessage(communityId: Long, message: String): NetworkResult<ForumMessage>
}
