package com.example.communityeventmanagement.domain.usecase.community

import com.example.communityeventmanagement.domain.repository.CommunityRepository
import com.example.communityeventmanagement.util.Resource
import javax.inject.Inject

class DeleteCommunity @Inject constructor(private val repository: CommunityRepository) {
    suspend operator fun invoke(communityId: Int): Resource<Unit> = repository.deleteCommunity(communityId)
}

