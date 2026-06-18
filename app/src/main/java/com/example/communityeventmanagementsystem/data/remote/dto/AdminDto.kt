package com.example.communityeventmanagementsystem.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DashboardStatsDto(
    @SerializedName("total_users") val totalUsers: Int,
    @SerializedName("total_communities") val totalCommunities: Int,
    @SerializedName("total_events") val totalEvents: Int,
    @SerializedName("total_organizers") val totalOrganizers: Int,
    @SerializedName("trusted_organizers") val trustedOrganizers: Int,
    @SerializedName("blocked_users") val blockedUsers: Int,
    @SerializedName("pending_trusted_applications") val pendingTrustedApplications: Int,
    @SerializedName("total_registrations") val totalRegistrations: Int
)

data class CreateUserRequest(
    val name: String,
    val email: String,
    val role: String,
    val password: String
)
