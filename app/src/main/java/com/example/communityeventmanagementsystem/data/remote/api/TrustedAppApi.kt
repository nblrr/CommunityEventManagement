package com.example.communityeventmanagementsystem.data.remote.api

import com.example.communityeventmanagementsystem.data.remote.dto.SubmitTrustedAppRequest
import com.example.communityeventmanagementsystem.data.remote.dto.TrustedAppDto
import retrofit2.http.*

interface TrustedAppApi {
    @GET("trusted-applications/me")
    suspend fun getMyApplication(): TrustedAppDto?

    @POST("trusted-applications")
    suspend fun submitApplication(@Body request: SubmitTrustedAppRequest): TrustedAppDto
}
