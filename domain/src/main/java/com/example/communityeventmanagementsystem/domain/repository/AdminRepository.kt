package com.example.communityeventmanagementsystem.domain.repository

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.domain.model.DashboardStats
import com.example.communityeventmanagementsystem.domain.model.TrustedApplication
import com.example.communityeventmanagementsystem.domain.model.User

interface AdminRepository {
    suspend fun getDashboardStats(): NetworkResult<DashboardStats>
    suspend fun getUsers(): NetworkResult<List<User>>
    suspend fun blockUser(id: Int): NetworkResult<Unit>
    suspend fun unblockUser(id: Int): NetworkResult<Unit>
    suspend fun getTrustedApplications(): NetworkResult<List<TrustedApplication>>
    suspend fun approveApplication(id: Int): NetworkResult<Unit>
    suspend fun rejectApplication(id: Int, notes: String): NetworkResult<Unit>
}
