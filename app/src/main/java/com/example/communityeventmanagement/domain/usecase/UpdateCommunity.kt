package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.entities.Community
import com.example.communityeventmanagement.domain.repository.CommunityRepository

class UpdateCommunity(private val repository: CommunityRepository) {
    suspend operator fun invoke(community: Community): Result<Unit> = repository.updateCommunity(community)
}
