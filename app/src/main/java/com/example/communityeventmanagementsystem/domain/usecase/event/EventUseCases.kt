package com.example.communityeventmanagementsystem.domain.usecase.event

import androidx.paging.PagingData
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.domain.model.Event
import com.example.communityeventmanagementsystem.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEventsUseCase @Inject constructor(private val repository: EventRepository) {
    operator fun invoke(categoryId: Long? = null, search: String? = null): Flow<PagingData<Event>> =
        repository.getEvents(categoryId, search)
}

class GetEventDetailUseCase @Inject constructor(private val repository: EventRepository) {
    suspend operator fun invoke(id: Long): NetworkResult<Event> = repository.getEventDetail(id)
}

class RegisterToEventUseCase @Inject constructor(private val repository: EventRepository) {
    suspend operator fun invoke(id: Long): NetworkResult<Unit> = repository.registerToEvent(id)
}

class UnregisterFromEventUseCase @Inject constructor(private val repository: EventRepository) {
    suspend operator fun invoke(id: Long): NetworkResult<Unit> = repository.unregisterFromEvent(id)
}

class GetMyRegisteredEventsUseCase @Inject constructor(private val repository: EventRepository) {
    suspend operator fun invoke(page: Int = 1): NetworkResult<List<Event>> = repository.getMyEvents(page)
}
