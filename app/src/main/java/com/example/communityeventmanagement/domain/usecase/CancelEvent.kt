package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.repository.CommunityRepository

/**
 * UseCase to handle canceling an event registration.
 */
class CancelEvent(private val repository: CommunityRepository) {
    suspend operator fun invoke(eventId: Int, communityId: Int, userId: String): Result<Unit> {
        return try {
            repository.toggleEventRegistration(communityId, eventId, userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
