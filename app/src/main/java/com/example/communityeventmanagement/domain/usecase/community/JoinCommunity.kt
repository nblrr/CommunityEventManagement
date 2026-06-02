package com.example.communityeventmanagement.domain.usecase.community

import com.example.communityeventmanagement.domain.repository.CommunityRepository
import com.example.communityeventmanagement.util.Resource
import javax.inject.Inject

/**
 * UseCase to join or leave a community.
 */
class JoinCommunity @Inject constructor(private val communityRepository: CommunityRepository) {
    suspend operator fun invoke(communityId: Int, userId: String): Resource<Unit> {
        return communityRepository.toggleCommunityJoin(communityId, userId)
    }
}

