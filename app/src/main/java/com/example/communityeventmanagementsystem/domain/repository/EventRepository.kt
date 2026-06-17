package com.example.communityeventmanagementsystem.domain.repository

import androidx.paging.PagingData
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.domain.model.Event
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getEvents(categoryId: Long? = null, search: String? = null): Flow<PagingData<Event>>
    suspend fun getEventDetail(id: Long): NetworkResult<Event>
    suspend fun registerToEvent(id: Long): NetworkResult<Unit>
    suspend fun unregisterFromEvent(id: Long): NetworkResult<Unit>
    suspend fun getMyEvents(page: Int): NetworkResult<List<Event>>
}
