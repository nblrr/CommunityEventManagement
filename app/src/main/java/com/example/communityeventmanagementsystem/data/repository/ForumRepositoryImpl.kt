package com.example.communityeventmanagementsystem.data.repository

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.network.ErrorHandler
import com.example.communityeventmanagementsystem.data.mapper.toDomain
import com.example.communityeventmanagementsystem.data.remote.api.ForumApi
import com.example.communityeventmanagementsystem.data.remote.dto.SendMessageRequest
import com.example.communityeventmanagementsystem.domain.model.ForumMessage
import com.example.communityeventmanagementsystem.domain.repository.ForumRepository
import javax.inject.Inject

class ForumRepositoryImpl @Inject constructor(
    private val api: ForumApi
) : ForumRepository {

    override suspend fun getMessages(communityId: Long): NetworkResult<List<ForumMessage>> {
        return try {
            val response = api.getMessages(communityId)
            NetworkResult.Success(response.data.map { it.toDomain() })
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun sendMessage(communityId: Long, message: String): NetworkResult<ForumMessage> {
        return try {
            val response = api.sendMessage(communityId, SendMessageRequest(message))
            NetworkResult.Success(response.toDomain())
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun deleteMessage(messageId: Long): NetworkResult<Unit> {
        return try {
            api.deleteMessage(messageId)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }
}
