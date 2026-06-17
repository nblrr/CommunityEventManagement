package com.example.communityeventmanagementsystem.data.remote.api

import com.example.communityeventmanagementsystem.data.remote.dto.DashboardStatsDto
import com.example.communityeventmanagementsystem.data.remote.dto.TrustedAppDto
import com.example.communityeventmanagementsystem.data.remote.dto.UserDto
import com.example.communityeventmanagementsystem.data.remote.dto.PaginatedResponse
import retrofit2.http.*

interface AdminApi {
    @GET("admin/dashboard")
    suspend fun getDashboardStats(): DashboardStatsDto

    @GET("admin/users")
    suspend fun getUsers(@Query("page") page: Int? = null): PaginatedResponse<UserDto>

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
}
