package com.example.communityeventmanagement.data.source.remote

import com.example.communityeventmanagement.data.dto.CommunityDto
import com.example.communityeventmanagement.data.dto.EventDto
import javax.inject.Inject

class CommunityRemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getCommunities(): List<CommunityDto> {
        val response = apiService.getCommunities()
        return if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
    }

    suspend fun getEvents(): List<EventDto> {
        val response = apiService.getEvents()
        return if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
    }
}
