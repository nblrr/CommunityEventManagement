package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.repository.CommunityRepository

/**
 * UseCase to join or leave a community.
 */
class JoinCommunity(private val communityRepository: CommunityRepository) {
    suspend operator fun invoke(communityId: Int, userId: String) {
        communityRepository.toggleCommunityJoin(communityId, userId)
    }
}
