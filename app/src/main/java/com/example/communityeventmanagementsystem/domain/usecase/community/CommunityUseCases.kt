package com.example.communityeventmanagementsystem.domain.usecase.community

import androidx.paging.PagingData
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.domain.model.Community
import com.example.communityeventmanagementsystem.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCommunitiesUseCase @Inject constructor(private val repository: CommunityRepository) {
    operator fun invoke(categoryId: Long? = null, search: String? = null): Flow<PagingData<Community>> =
        repository.getCommunities(categoryId, search)
}

class GetCommunityDetailUseCase @Inject constructor(private val repository: CommunityRepository) {
    suspend operator fun invoke(id: Long): NetworkResult<Community> = repository.getCommunityDetail(id)
}

class JoinCommunityUseCase @Inject constructor(private val repository: CommunityRepository) {
    suspend operator fun invoke(id: Long): NetworkResult<Unit> = repository.joinCommunity(id)
}

class LeaveCommunityUseCase @Inject constructor(private val repository: CommunityRepository) {
    suspend operator fun invoke(id: Long): NetworkResult<Unit> = repository.leaveCommunity(id)
}
