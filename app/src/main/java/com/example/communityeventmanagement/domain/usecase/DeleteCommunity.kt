package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.repository.CommunityRepository

class DeleteCommunity(private val repository: CommunityRepository) {
    suspend operator fun invoke(communityId: Int): Result<Unit> = repository.deleteCommunity(communityId)
}
