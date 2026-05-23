package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.StateFlow

class GetJoinedCommunityIds(private val repository: CommunityRepository) {
    operator fun invoke(): StateFlow<Set<Int>> = repository.joinedCommunityIds
}
