package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.repository.CommunityRepository

class DeleteEvent(private val repository: CommunityRepository) {
    suspend operator fun invoke(communityId: Int, eventId: Int): Result<Unit> = repository.deleteEvent(communityId, eventId)
}
