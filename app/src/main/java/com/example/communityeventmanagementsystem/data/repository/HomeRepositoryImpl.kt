package com.example.communityeventmanagementsystem.data.repository

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.data.mapper.toDomain
import com.example.communityeventmanagementsystem.data.remote.api.HomeApi
import com.example.communityeventmanagementsystem.domain.model.Category
import com.example.communityeventmanagementsystem.domain.model.Community
import com.example.communityeventmanagementsystem.domain.model.Event
import com.example.communityeventmanagementsystem.domain.repository.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val api: HomeApi
) : HomeRepository {

    override suspend fun getCategories(): NetworkResult<List<Category>> {
        return try {
            val response = api.getCategories()
            NetworkResult.Success(response.map { it.toDomain() })
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun getUpcomingEvents(): NetworkResult<List<Event>> {
        return try {
            val response = api.getUpcomingEvents()
            NetworkResult.Success(response.map { it.toDomain() })
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun getRecommendedEvents(): NetworkResult<List<Event>> {
        return try {
            val response = api.getRecommendedEvents()
            NetworkResult.Success(response.map { it.toDomain() })
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun getMyCommunities(): NetworkResult<List<Community>> {
        return try {
            val response = api.getMyCommunities()
            NetworkResult.Success(response.data.map { it.toDomain() })
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An unknown error occurred")
        }
    }
}
