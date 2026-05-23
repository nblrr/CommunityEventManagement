package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.entities.Event
import com.example.communityeventmanagement.domain.repository.CommunityRepository

/**
 * UseCase to get recommended events based on a filter.
 */
class GetRecommendedEvents(private val communityRepository: CommunityRepository) {
    operator fun invoke(filter: (String) -> Boolean): List<Event> {
        return communityRepository.getRecommendedEvents(filter)
    }
}
