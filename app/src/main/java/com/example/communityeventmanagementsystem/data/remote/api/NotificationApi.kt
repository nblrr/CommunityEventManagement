package com.example.communityeventmanagementsystem.data.remote.api

import com.example.communityeventmanagementsystem.data.remote.dto.NotificationDto
import com.example.communityeventmanagementsystem.data.remote.dto.PaginatedResponse
import retrofit2.http.*

interface NotificationApi {
    @GET("notifications")
    suspend fun getNotifications(@Query("page") page: Int? = null): PaginatedResponse<NotificationDto>

    @POST("notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: Long)
}
