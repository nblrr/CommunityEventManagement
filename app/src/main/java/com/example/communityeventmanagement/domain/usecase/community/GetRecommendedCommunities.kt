package com.example.communityeventmanagement.domain.usecase.community

import com.example.communityeventmanagement.domain.model.Community
import com.example.communityeventmanagement.domain.repository.CommunityRepository
import javax.inject.Inject

/**
 * UseCase to get recommended communities for the user.
 */
class GetRecommendedCommunities @Inject constructor(private val communityRepository: CommunityRepository) {
    operator fun invoke(): List<Community> {
        return communityRepository.getRecommendedCommunities()
    }
}

