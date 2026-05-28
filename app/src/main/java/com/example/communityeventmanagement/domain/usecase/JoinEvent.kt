package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.repository.CommunityRepository
import com.example.communityeventmanagement.domain.util.Resource
import javax.inject.Inject

/**
 * UseCase to handle joining an event.
 */
class JoinEvent @Inject constructor(private val repository: CommunityRepository) {
    suspend operator fun invoke(eventId: Int, communityId: Int, userId: String): Resource<Unit> {
        return repository.toggleEventRegistration(communityId, eventId, userId)
    }
}
