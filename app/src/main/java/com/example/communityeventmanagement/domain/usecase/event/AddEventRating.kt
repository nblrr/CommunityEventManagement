package com.example.communityeventmanagement.domain.usecase.event

import com.example.communityeventmanagement.domain.repository.CommunityRepository
import com.example.communityeventmanagement.util.Resource
import javax.inject.Inject

/**
 * UseCase to handle adding a rating and review to an event.
 */
class AddEventRating @Inject constructor(private val repository: CommunityRepository) {
    suspend operator fun invoke(
        communityId: Int,
        eventId: Int,
        userId: String,
        userName: String,
        score: Int,
        comment: String
    ): Resource<Unit> {
        return repository.addEventRating(communityId, eventId, userId, userName, score, comment)
    }
}

