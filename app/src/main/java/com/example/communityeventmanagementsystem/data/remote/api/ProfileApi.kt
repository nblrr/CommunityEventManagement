package com.example.communityeventmanagementsystem.data.remote.api

import com.example.communityeventmanagementsystem.data.remote.dto.UserDto
import com.example.communityeventmanagementsystem.data.remote.dto.BecomeOrganizerResponse
import okhttp3.MultipartBody
import retrofit2.http.*

interface ProfileApi {
    @GET("user")
    suspend fun getProfile(): UserDto

    @PUT("profile")
    suspend fun updateProfile(@Body profile: UserDto): UserDto

    @Multipart
    @POST("profile/avatar")
    suspend fun uploadAvatar(@Part avatar: MultipartBody.Part): UserDto

    @POST("become-organizer")
    suspend fun becomeOrganizer(): BecomeOrganizerResponse
}
