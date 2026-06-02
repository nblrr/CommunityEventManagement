package com.example.communityeventmanagement.domain.usecase.event

import com.example.communityeventmanagement.domain.model.Event
import com.example.communityeventmanagement.domain.repository.CommunityRepository
import com.example.communityeventmanagement.util.Resource
import javax.inject.Inject

class UpdateEvent @Inject constructor(private val repository: CommunityRepository) {
    suspend operator fun invoke(communityId: Int, event: Event): Resource<Unit> = repository.updateEvent(communityId, event)
}

