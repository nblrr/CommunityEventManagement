package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.entities.Event
import com.example.communityeventmanagement.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetEventDetail(private val repository: CommunityRepository) {
    operator fun invoke(eventId: Int, communityId: Int): Flow<Event?> = repository.communities.map { communities ->
        communities.find { it.id == communityId }?.events?.find { it.id == eventId }
    }
}
