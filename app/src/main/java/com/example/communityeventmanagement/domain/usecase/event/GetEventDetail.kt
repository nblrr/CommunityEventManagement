package com.example.communityeventmanagement.domain.usecase.event

import com.example.communityeventmanagement.domain.model.Event
import com.example.communityeventmanagement.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetEventDetail @Inject constructor(private val repository: CommunityRepository) {
    operator fun invoke(eventId: Int, communityId: Int): Flow<Event?> = repository.communities.map { communities ->
        communities.find { it.id == communityId }?.events?.find { it.id == eventId }
    }
}

