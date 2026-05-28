package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.entities.Event
import com.example.communityeventmanagement.domain.repository.CommunityRepository
import javax.inject.Inject

/**
 * UseCase to get recommended events based on a filter.
 */
class GetRecommendedEvents @Inject constructor(private val communityRepository: CommunityRepository) {
    operator fun invoke(filter: (String) -> Boolean): List<Event> {
        return communityRepository.getRecommendedEvents(filter)
    }
}
