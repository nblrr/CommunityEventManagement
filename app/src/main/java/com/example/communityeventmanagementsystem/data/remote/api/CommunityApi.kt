package com.example.communityeventmanagementsystem.data.remote.api

import com.example.communityeventmanagementsystem.data.remote.dto.CommunityDto
import retrofit2.http.*

interface CommunityApi {
    @GET("communities")
    suspend fun getCommunities(
        @Query("page") page: Int,
        @Query("category_id") categoryId: Long? = null,
        @Query("search") search: String? = null,
        @Query("sort_by") sortBy: String? = null
    ): CommunityListResponse

    @GET("communities/{id}")
    suspend fun getCommunityDetail(@Path("id") id: Long): CommunityDto

    @POST("communities/{id}/join")
    suspend fun joinCommunity(@Path("id") id: Long)

    @POST("communities/{id}/leave")
    suspend fun leaveCommunity(@Path("id") id: Long)
}

data class CommunityListResponse(
    val data: List<CommunityDto>,
    val current_page: Int,
    val last_page: Int
)
