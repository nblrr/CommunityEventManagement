package com.example.communityeventmanagement.domain.usecase.event

import com.example.communityeventmanagement.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * UseCase to get the IDs of events the user has registered for.
 */
class GetRegisteredEventIds @Inject constructor(private val communityRepository: CommunityRepository) {
    operator fun invoke(): StateFlow<Set<Int>> = communityRepository.registeredEventIds
}

