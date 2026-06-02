package com.example.communityeventmanagement.domain.usecase.event

import com.example.communityeventmanagement.domain.repository.CommunityRepository
import com.example.communityeventmanagement.util.Resource
import javax.inject.Inject

class DeleteEvent @Inject constructor(private val repository: CommunityRepository) {
    suspend operator fun invoke(communityId: Int, eventId: Int): Resource<Unit> = repository.deleteEvent(communityId, eventId)
}

