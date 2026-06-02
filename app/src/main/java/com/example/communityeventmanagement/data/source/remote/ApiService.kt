package com.example.communityeventmanagement.data.source.remote

import com.example.communityeventmanagement.data.dto.CommunityDto
import com.example.communityeventmanagement.data.dto.EventDto
import com.example.communityeventmanagement.data.dto.UserDto
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("users/current")
    suspend fun getCurrentUser(): Response<UserDto>

    @GET("communities")
    suspend fun getCommunities(): Response<List<CommunityDto>>

    @GET("events")
    suspend fun getEvents(): Response<List<EventDto>>
}
