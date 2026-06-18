package com.example.communityeventmanagementsystem.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.network.ErrorHandler
import com.example.communityeventmanagementsystem.data.mapper.toDomain
import com.example.communityeventmanagementsystem.data.remote.api.EventApi
import com.example.communityeventmanagementsystem.data.remote.api.RateEventRequest
import com.example.communityeventmanagementsystem.data.remote.paging.EventPagingSource
import com.example.communityeventmanagementsystem.domain.model.Event
import com.example.communityeventmanagementsystem.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val api: EventApi
) : EventRepository {

    override fun getEvents(
        categoryId: Long?,
        search: String?,
        status: String?,
        sortBy: String?
    ): Flow<PagingData<Event>> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { EventPagingSource(api, categoryId, search, status, sortBy) }
        ).flow
    }

    override suspend fun getEventDetail(id: Long): NetworkResult<Event> {
        return try {
            val response = api.getEventDetail(id)
            NetworkResult.Success(response.toDomain())
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun registerToEvent(id: Long): NetworkResult<Unit> {
        return try {
            api.registerToEvent(id)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun unregisterFromEvent(id: Long): NetworkResult<Unit> {
        return try {
            api.unregisterFromEvent(id)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun getMyEvents(page: Int): NetworkResult<List<Event>> {
        return try {
            val response = api.getMyEvents(page)
            NetworkResult.Success(response.data.map { it.toDomain() })
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun rateEvent(id: Long, rating: Int, comment: String): NetworkResult<Unit> {
        return try {
            api.rateEvent(id, RateEventRequest(rating, comment))
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun getEventParticipants(id: Long): NetworkResult<List<com.example.communityeventmanagementsystem.domain.model.User>> {
        return try {
            val response = api.getEventParticipants(id)
            NetworkResult.Success(response.map { it.toDomain() })
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }
}
