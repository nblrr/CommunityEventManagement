package com.example.communityeventmanagementsystem.data.repository

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.network.ErrorHandler
import com.example.communityeventmanagementsystem.data.mapper.toDomain
import com.example.communityeventmanagementsystem.data.remote.api.OrganizerApi
import com.example.communityeventmanagementsystem.data.remote.dto.CommunityDto
import com.example.communityeventmanagementsystem.data.remote.dto.EventDto
import com.example.communityeventmanagementsystem.data.remote.dto.CreateCommunityRequest
import com.example.communityeventmanagementsystem.data.remote.dto.CreateEventRequest
import com.example.communityeventmanagementsystem.domain.model.Community
import com.example.communityeventmanagementsystem.domain.model.Event
import com.example.communityeventmanagementsystem.domain.repository.OrganizerRepository
import javax.inject.Inject

class OrganizerRepositoryImpl @Inject constructor(
    private val api: OrganizerApi
) : OrganizerRepository {

    override suspend fun getMyCommunities(): NetworkResult<List<Community>> {
        return try {
            val response = api.getMyCommunities()
            NetworkResult.Success(response.data.map { it.toDomain() })
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun getMyEvents(): NetworkResult<List<Event>> {
        return try {
            val response = api.getMyEvents()
            NetworkResult.Success(response.data.map { it.toDomain() })
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun createCommunity(community: Community): NetworkResult<Community> {
        return try {
            val request = CreateCommunityRequest(
                name = community.name,
                description = community.description ?: "",
                categoryId = community.categoryId,
                coverImageUrl = community.coverImageUrl
            )
            val response = api.createCommunity(request)
            NetworkResult.Success(response.toDomain())
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun updateCommunity(id: Long, community: Community): NetworkResult<Community> {
        return try {
            val dto = CommunityDto(
                id = id,
                name = community.name,
                description = community.description,
                organizerId = 0L,
                categoryId = community.categoryId,
                memberCount = community.memberCount,
                coverImageUrl = community.coverImageUrl
            )
            val response = api.updateCommunity(id, dto)
            NetworkResult.Success(response.toDomain())
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun deleteCommunity(id: Long): NetworkResult<Unit> {
        return try {
            api.deleteCommunity(id)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun createEvent(event: Event): NetworkResult<Event> {
        return try {
            val request = CreateEventRequest(
                communityId = event.communityId,
                categoryId = event.categoryId,  
                title = event.title,
                description = event.description ?: "",
                eventDate = event.eventDate,
                eventTime = event.eventTime, 
                location = event.location,
                maxAttendees = event.maxAttendees,
                isOnline = event.isOnline,
                coverImageUrl = event.coverImageUrl
            )
            val response = api.createEvent(request)
            NetworkResult.Success(response.toDomain())
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun updateEvent(id: Long, event: Event): NetworkResult<Event> {
        return try {
            val dto = EventDto(
                id = id,
                title = event.title,
                description = event.description,
                eventDate = event.eventDate,
                location = null,
                attendeeCount = event.attendeeCount,
                maxAttendees = event.maxAttendees,
                status = event.status,
                coverImageUrl = event.coverImageUrl
            )
            val response = api.updateEvent(id, dto)
            NetworkResult.Success(response.toDomain())
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }

    override suspend fun deleteEvent(id: Long): NetworkResult<Unit> {
        return try {
            api.deleteEvent(id)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }
}
