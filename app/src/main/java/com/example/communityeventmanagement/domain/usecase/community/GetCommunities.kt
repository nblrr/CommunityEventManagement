package com.example.communityeventmanagement.domain.usecase.community

import com.example.communityeventmanagement.domain.model.Community
import com.example.communityeventmanagement.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * UseCase to get the list of communities.
 */
class GetCommunities @Inject constructor(private val communityRepository: CommunityRepository) {
    operator fun invoke(): StateFlow<List<Community>> {
        return communityRepository.communities
    }
}

