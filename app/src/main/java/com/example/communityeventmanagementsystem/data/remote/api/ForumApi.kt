package com.example.communityeventmanagementsystem.data.remote.api

import com.example.communityeventmanagementsystem.data.remote.dto.ForumMessageDto
import com.example.communityeventmanagementsystem.data.remote.dto.PaginatedResponse
import com.example.communityeventmanagementsystem.data.remote.dto.SendMessageRequest
import retrofit2.http.*

interface ForumApi {
    @GET("communities/{communityId}/messages")
    suspend fun getMessages(
        @Path("communityId") communityId: Long,
        @Query("page") page: Int? = null
    ): PaginatedResponse<ForumMessageDto>

    @POST("communities/{communityId}/messages")
    suspend fun sendMessage(
        @Path("communityId") communityId: Long,
        @Body request: SendMessageRequest
    ): ForumMessageDto

    @DELETE("forum-messages/{messageId}")
    suspend fun deleteMessage(
        @Path("messageId") messageId: Long
    )
}
