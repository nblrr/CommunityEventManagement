package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.entities.Event
import com.example.communityeventmanagement.domain.repository.CommunityRepository

class CreateEvent(private val repository: CommunityRepository) {
    suspend operator fun invoke(communityId: Int, event: Event): Result<Unit> = repository.addEvent(communityId, event)
}
