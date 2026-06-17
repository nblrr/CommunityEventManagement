package com.example.communityeventmanagementsystem.domain.repository

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.domain.model.Community
import com.example.communityeventmanagementsystem.domain.model.Event

interface OrganizerRepository {
    suspend fun getMyCommunities(): NetworkResult<List<Community>>
    suspend fun getMyEvents(): NetworkResult<List<Event>>
    suspend fun createCommunity(community: Community): NetworkResult<Community>
    suspend fun updateCommunity(id: Long, community: Community): NetworkResult<Community>
    suspend fun deleteCommunity(id: Long): NetworkResult<Unit>
    suspend fun createEvent(event: Event): NetworkResult<Event>
    suspend fun updateEvent(id: Long, event: Event): NetworkResult<Event>
    suspend fun deleteEvent(id: Long): NetworkResult<Unit>
}
