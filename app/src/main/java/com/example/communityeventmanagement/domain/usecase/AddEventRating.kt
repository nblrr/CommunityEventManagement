package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.repository.CommunityRepository

/**
 * UseCase to handle adding a rating and review to an event.
 */
class AddEventRating(private val repository: CommunityRepository) {
    suspend operator fun invoke(
        communityId: Int,
        eventId: Int,
        userId: String,
        userName: String,
        score: Int,
        comment: String
    ): Result<Unit> {
        return try {
            repository.addEventRating(communityId, eventId, userId, userName, score, comment)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
