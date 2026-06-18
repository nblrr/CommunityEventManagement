package com.example.communityeventmanagementsystem.data.remote.api

import com.example.communityeventmanagementsystem.data.remote.dto.DashboardStatsDto
import com.example.communityeventmanagementsystem.data.remote.dto.TrustedAppDto
import com.example.communityeventmanagementsystem.data.remote.dto.UserDto
import com.example.communityeventmanagementsystem.data.remote.dto.PaginatedResponse
import com.example.communityeventmanagementsystem.data.remote.dto.CreateUserRequest
import retrofit2.http.*

interface AdminApi {
    @GET("admin/dashboard")
    suspend fun getDashboardStats(): DashboardStatsDto

    @GET("admin/users")
    suspend fun getUsers(
        @Query("search") search: String? = null,
        @Query("role") role: String? = null,
        @Query("status") status: String? = null,
        @Query("page") page: Int? = null
    ): PaginatedResponse<UserDto>

    @POST("admin/users")
    suspend fun createUser(@Body request: CreateUserRequest): UserDto

    @DELETE("admin/users/{id}")
    suspend fun deleteUser(@Path("id") id: Long)

    @POST("admin/users/{id}/role")
    suspend fun updateRole(@Path("id") id: Long, @Query("role") role: String): UserDto

    @POST("admin/users/{id}/revoke-trusted")
    suspend fun revokeTrusted(@Path("id") id: Long)

    @POST("admin/users/{id}/block")
    suspend fun blockUser(@Path("id") id: Long)

    @POST("admin/users/{id}/unblock")
    suspend fun unblockUser(@Path("id") id: Long)

    @GET("admin/trusted-applications")
    suspend fun getTrustedApplications(@Query("page") page: Int? = null): PaginatedResponse<TrustedAppDto>

    @POST("admin/trusted-applications/{id}/approve")
    suspend fun approveApplication(@Path("id") id: Long)

    @POST("admin/trusted-applications/{id}/reject")
    suspend fun rejectApplication(@Path("id") id: Long, @Query("notes") notes: String)

    @GET("communities")
    suspend fun getCommunities(
        @Query("search") search: String? = null
    ): com.example.communityeventmanagementsystem.data.remote.api.CommunityListResponse

    @GET("events")
    suspend fun getEvents(
        @Query("search") search: String? = null
    ): com.example.communityeventmanagementsystem.data.remote.api.EventListResponse

    @DELETE("communities/{id}")
    suspend fun deleteCommunity(@Path("id") id: Long)

    @DELETE("events/{id}")
    suspend fun deleteEvent(@Path("id") id: Long)
}
