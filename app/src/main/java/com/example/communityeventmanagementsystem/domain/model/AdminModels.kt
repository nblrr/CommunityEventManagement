package com.example.communityeventmanagementsystem.domain.model

data class DashboardStats(
    val totalUsers: Int,
    val totalCommunities: Int,
    val totalEvents: Int,
    val totalOrganizers: Int,
    val trustedOrganizers: Int,
    val blockedUsers: Int,
    val pendingTrustedApplications: Int,
    val totalRegistrations: Int
)
