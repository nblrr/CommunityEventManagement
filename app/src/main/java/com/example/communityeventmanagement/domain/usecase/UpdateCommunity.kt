package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.entities.Community
import com.example.communityeventmanagement.domain.repository.CommunityRepository
import com.example.communityeventmanagement.domain.util.Resource
import javax.inject.Inject

class UpdateCommunity @Inject constructor(private val repository: CommunityRepository) {
    suspend operator fun invoke(community: Community): Resource<Unit> = repository.updateCommunity(community)
}
