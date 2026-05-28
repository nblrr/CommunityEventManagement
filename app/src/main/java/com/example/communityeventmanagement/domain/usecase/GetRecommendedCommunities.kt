package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.entities.Community
import com.example.communityeventmanagement.domain.repository.CommunityRepository
import javax.inject.Inject

/**
 * UseCase to get recommended communities for the user.
 */
class GetRecommendedCommunities @Inject constructor(private val communityRepository: CommunityRepository) {
    suspend operator fun invoke(): List<Community> {
        return communityRepository.getRecommendedCommunities()
    }
}
