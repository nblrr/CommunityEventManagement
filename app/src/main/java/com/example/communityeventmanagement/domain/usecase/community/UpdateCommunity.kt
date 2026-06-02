package com.example.communityeventmanagement.domain.usecase.community

import com.example.communityeventmanagement.domain.model.Community
import com.example.communityeventmanagement.domain.repository.CommunityRepository
import com.example.communityeventmanagement.util.Resource
import javax.inject.Inject

class UpdateCommunity @Inject constructor(private val repository: CommunityRepository) {
    suspend operator fun invoke(community: Community): Resource<Unit> = repository.updateCommunity(community)
}

