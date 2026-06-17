package com.example.communityeventmanagementsystem.data.repository

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.data.mapper.toDomain
import com.example.communityeventmanagementsystem.data.remote.api.AdminApi
import com.example.communityeventmanagementsystem.domain.model.DashboardStats
import com.example.communityeventmanagementsystem.domain.model.TrustedApplication
import com.example.communityeventmanagementsystem.domain.model.User
import com.example.communityeventmanagementsystem.domain.repository.AdminRepository
import javax.inject.Inject

class AdminRepositoryImpl @Inject constructor(
    private val api: AdminApi
) : AdminRepository {

    override suspend fun getDashboardStats(): NetworkResult<DashboardStats> {
        return try {
            val response = api.getDashboardStats()
            NetworkResult.Success(
                DashboardStats(
                    totalUsers = response.totalUsers,
                    totalCommunities = response.totalCommunities,
                    totalEvents = response.totalEvents,
                    totalOrganizers = response.totalOrganizers,
                    trustedOrganizers = response.trustedOrganizers,
                    blockedUsers = response.blockedUsers,
                    pendingTrustedApplications = response.pendingTrustedApplications
                )
            )
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun getUsers(): NetworkResult<List<User>> {
        return try {
            val response = api.getUsers()
            NetworkResult.Success(response.data.map { it.toDomain() })
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun blockUser(id: Long): NetworkResult<Unit> {
        return try {
            api.blockUser(id)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun unblockUser(id: Long): NetworkResult<Unit> {
        return try {
            api.unblockUser(id)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun getTrustedApplications(): NetworkResult<List<TrustedApplication>> {
        return try {
            val response = api.getTrustedApplications()
            NetworkResult.Success(response.data.map { it.toDomain() })
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun approveApplication(id: Long): NetworkResult<Unit> {
        return try {
            api.approveApplication(id)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun rejectApplication(id: Long, notes: String): NetworkResult<Unit> {
        return try {
            api.rejectApplication(id, notes)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An unknown error occurred")
        }
    }
}
