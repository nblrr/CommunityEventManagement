package com.example.communityeventmanagementsystem.domain.repository

import androidx.paging.PagingData
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.domain.model.Community
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    fun getCommunities(categoryId: Long? = null, search: String? = null): Flow<PagingData<Community>>
    suspend fun getCommunityDetail(id: Long): NetworkResult<Community>
    suspend fun joinCommunity(id: Long): NetworkResult<Unit>
    suspend fun leaveCommunity(id: Long): NetworkResult<Unit>
}
