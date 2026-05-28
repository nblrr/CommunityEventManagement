package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.entities.Event
import com.example.communityeventmanagement.domain.repository.CommunityRepository
import com.example.communityeventmanagement.domain.util.Resource
import javax.inject.Inject

class UpdateEvent @Inject constructor(private val repository: CommunityRepository) {
    suspend operator fun invoke(communityId: Int, event: Event): Resource<Unit> = repository.updateEvent(communityId, event)
}
