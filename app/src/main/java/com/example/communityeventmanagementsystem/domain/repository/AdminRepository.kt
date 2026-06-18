package com.example.communityeventmanagementsystem.domain.repository

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.domain.model.DashboardStats
import com.example.communityeventmanagementsystem.domain.model.TrustedApplication
import com.example.communityeventmanagementsystem.domain.model.User

interface AdminRepository {
    suspend fun getDashboardStats(): NetworkResult<DashboardStats>
    suspend fun getUsers(search: String? = null, role: String? = null, status: String? = null): NetworkResult<List<User>>
    suspend fun createUser(name: String, email: String, role: String, password: String): NetworkResult<User>
    suspend fun deleteUser(id: Long): NetworkResult<Unit>
    suspend fun updateRole(id: Long, role: String): NetworkResult<User>
    suspend fun revokeTrusted(id: Long): NetworkResult<Unit>
    suspend fun blockUser(id: Long): NetworkResult<Unit>
    suspend fun unblockUser(id: Long): NetworkResult<Unit>
    suspend fun getTrustedApplications(): NetworkResult<List<TrustedApplication>>
    suspend fun approveApplication(id: Long): NetworkResult<Unit>
    suspend fun rejectApplication(id: Long, notes: String): NetworkResult<Unit>
    suspend fun getCommunities(search: String? = null): NetworkResult<List<com.example.communityeventmanagementsystem.domain.model.Community>>
    suspend fun getEvents(search: String? = null): NetworkResult<List<com.example.communityeventmanagementsystem.domain.model.Event>>
    suspend fun deleteCommunity(id: Long): NetworkResult<Unit>
    suspend fun deleteEvent(id: Long): NetworkResult<Unit>
}
