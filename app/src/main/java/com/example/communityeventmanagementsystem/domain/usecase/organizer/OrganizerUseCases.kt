package com.example.communityeventmanagementsystem.domain.usecase.organizer

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.domain.model.Community
import com.example.communityeventmanagementsystem.domain.model.Event
import com.example.communityeventmanagementsystem.domain.repository.OrganizerRepository
import javax.inject.Inject

class GetMyManagedCommunitiesUseCase @Inject constructor(private val repository: OrganizerRepository) {
    suspend operator fun invoke(): NetworkResult<List<Community>> = repository.getMyCommunities()
}

class GetMyManagedEventsUseCase @Inject constructor(private val repository: OrganizerRepository) {
    suspend operator fun invoke(): NetworkResult<List<Event>> = repository.getMyEvents()
}

class CreateCommunityUseCase @Inject constructor(private val repository: OrganizerRepository) {
    suspend operator fun invoke(community: Community): NetworkResult<Community> = repository.createCommunity(community)
}

class DeleteEventUseCase @Inject constructor(private val repository: OrganizerRepository) {
    suspend operator fun invoke(id: Long): NetworkResult<Unit> = repository.deleteEvent(id)
}

class CreateEventUseCase @Inject constructor(private val repository: OrganizerRepository) {
    suspend operator fun invoke(event: Event): NetworkResult<Event> = repository.createEvent(event)
}
