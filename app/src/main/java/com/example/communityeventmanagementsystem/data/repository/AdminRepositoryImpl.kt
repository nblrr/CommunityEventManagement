package com.example.communityeventmanagementsystem.data.repository

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.network.ErrorHandler
import com.example.communityeventmanagementsystem.data.mapper.toDomain
import com.example.communityeventmanagementsystem.data.remote.api.AdminApi
import com.example.communityeventmanagementsystem.data.remote.dto.CreateUserRequest
import com.example.communityeventmanagementsystem.domain.model.Community
import com.example.communityeventmanagementsystem.domain.model.Event
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
                    pendingTrustedApplications = response.pendingTrustedApplications,
                    totalRegistrations = response.totalRegistrations
                )
            )
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun getUsers(search: String?, role: String?, status: String?): NetworkResult<List<User>> {
        return try {
            val response = api.getUsers(search, role, status)
            NetworkResult.Success(response.data.map { it.toDomain() })
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun createUser(name: String, email: String, role: String, password: String): NetworkResult<User> {
        return try {
            val response = api.createUser(CreateUserRequest(name, email, role, password))
            NetworkResult.Success(response.toDomain())
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun deleteUser(id: Long): NetworkResult<Unit> {
        return try {
            api.deleteUser(id)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun updateRole(id: Long, role: String): NetworkResult<User> {
        return try {
            val response = api.updateRole(id, role)
            NetworkResult.Success(response.toDomain())
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun revokeTrusted(id: Long): NetworkResult<Unit> {
        return try {
            api.revokeTrusted(id)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun blockUser(id: Long): NetworkResult<Unit> {
        return try {
            api.blockUser(id)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun unblockUser(id: Long): NetworkResult<Unit> {
        return try {
            api.unblockUser(id)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun getTrustedApplications(): NetworkResult<List<TrustedApplication>> {
        return try {
            val response = api.getTrustedApplications()
            NetworkResult.Success(response.data.map { it.toDomain() })
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun approveApplication(id: Long): NetworkResult<Unit> {
        return try {
            api.approveApplication(id)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun rejectApplication(id: Long, notes: String): NetworkResult<Unit> {
        return try {
            api.rejectApplication(id, notes)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun getCommunities(search: String?): NetworkResult<List<Community>> {
        return try {
            val response = api.getCommunities(search)
            NetworkResult.Success(response.data.map { it.toDomain() })
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun getEvents(search: String?): NetworkResult<List<Event>> {
        return try {
            val response = api.getEvents(search)
            NetworkResult.Success(response.data.map { it.toDomain() })
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun deleteCommunity(id: Long): NetworkResult<Unit> {
        return try {
            api.deleteCommunity(id)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun deleteEvent(id: Long): NetworkResult<Unit> {
        return try {
            api.deleteEvent(id)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }
}
