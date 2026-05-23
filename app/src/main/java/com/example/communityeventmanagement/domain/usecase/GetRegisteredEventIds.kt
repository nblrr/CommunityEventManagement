package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.StateFlow

/**
 * UseCase to get the IDs of events the user has registered for.
 */
class GetRegisteredEventIds(private val communityRepository: CommunityRepository) {
    operator fun invoke(): StateFlow<Set<Int>> = communityRepository.registeredEventIds
}
