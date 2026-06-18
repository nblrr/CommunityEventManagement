package com.example.communityeventmanagementsystem.data.remote.api

import com.example.communityeventmanagementsystem.data.remote.dto.CommunityDto
import com.example.communityeventmanagementsystem.data.remote.dto.EventDto
import com.example.communityeventmanagementsystem.data.remote.dto.PaginatedResponse
import com.example.communityeventmanagementsystem.data.remote.dto.CreateCommunityRequest
import com.example.communityeventmanagementsystem.data.remote.dto.CreateEventRequest
import retrofit2.http.*

interface OrganizerApi {
    @GET("organizer/communities")
    suspend fun getMyCommunities(@Query("page") page: Int? = null): PaginatedResponse<CommunityDto>

    @GET("organizer/events")
    suspend fun getMyEvents(@Query("page") page: Int? = null): PaginatedResponse<EventDto>

    @POST("communities")
    suspend fun createCommunity(@Body community: CreateCommunityRequest): CommunityDto

    @PUT("communities/{id}")
    suspend fun updateCommunity(@Path("id") id: Long, @Body community: CommunityDto): CommunityDto

    @DELETE("communities/{id}")
    suspend fun deleteCommunity(@Path("id") id: Long)

    @POST("events")
    suspend fun createEvent(@Body event: CreateEventRequest): EventDto

    @PUT("events/{id}")
    suspend fun updateEvent(@Path("id") id: Long, @Body event: CreateEventRequest): EventDto

    @DELETE("events/{id}")
    suspend fun deleteEvent(@Path("id") id: Long)
}
