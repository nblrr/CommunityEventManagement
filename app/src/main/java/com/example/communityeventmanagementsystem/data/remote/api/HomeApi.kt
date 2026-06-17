package com.example.communityeventmanagementsystem.data.remote.api

import com.example.communityeventmanagementsystem.data.remote.dto.CategoryDto
import com.example.communityeventmanagementsystem.data.remote.dto.CommunityDto
import com.example.communityeventmanagementsystem.data.remote.dto.EventDto
import com.example.communityeventmanagementsystem.data.remote.dto.PaginatedResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeApi {
    @GET("categories")
    suspend fun getCategories(): List<CategoryDto>

    @GET("upcoming-events")
    suspend fun getUpcomingEvents(): List<EventDto>

    @GET("recommended-events")
    suspend fun getRecommendedEvents(): List<EventDto>

    @GET("my-communities")
    suspend fun getMyCommunities(@Query("page") page: Int? = null): PaginatedResponse<CommunityDto>

}
