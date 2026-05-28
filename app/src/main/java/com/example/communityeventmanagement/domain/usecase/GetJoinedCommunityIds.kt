package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetJoinedCommunityIds @Inject constructor(private val repository: CommunityRepository) {
    operator fun invoke(): StateFlow<Set<Int>> = repository.joinedCommunityIds
}
