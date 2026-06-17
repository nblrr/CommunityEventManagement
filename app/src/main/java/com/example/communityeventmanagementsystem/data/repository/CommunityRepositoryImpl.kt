package com.example.communityeventmanagementsystem.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.network.ErrorHandler
import com.example.communityeventmanagementsystem.data.mapper.toDomain
import com.example.communityeventmanagementsystem.data.remote.api.CommunityApi
import com.example.communityeventmanagementsystem.data.remote.paging.CommunityPagingSource
import com.example.communityeventmanagementsystem.domain.model.Community
import com.example.communityeventmanagementsystem.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CommunityRepositoryImpl @Inject constructor(
    private val api: CommunityApi
) : CommunityRepository {

    override fun getCommunities(categoryId: Long?, search: String?): Flow<PagingData<Community>> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { CommunityPagingSource(api, categoryId, search) }
        ).flow
    }

    override suspend fun getCommunityDetail(id: Long): NetworkResult<Community> {
        return try {
            val response = api.getCommunityDetail(id)
            NetworkResult.Success(response.toDomain())
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun joinCommunity(id: Long): NetworkResult<Unit> {
        return try {
            api.joinCommunity(id)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun leaveCommunity(id: Long): NetworkResult<Unit> {
        return try {
            api.leaveCommunity(id)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }
}
