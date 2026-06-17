package com.example.communityeventmanagementsystem.domain.repository

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.domain.model.Category
import com.example.communityeventmanagementsystem.domain.model.Community
import com.example.communityeventmanagementsystem.domain.model.Event

interface HomeRepository {
    suspend fun getCategories(): NetworkResult<List<Category>>
    suspend fun getUpcomingEvents(): NetworkResult<List<Event>>
    suspend fun getRecommendedEvents(): NetworkResult<List<Event>>
    suspend fun getMyCommunities(): NetworkResult<List<Community>>
}
