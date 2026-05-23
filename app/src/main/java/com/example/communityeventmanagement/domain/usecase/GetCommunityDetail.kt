package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.entities.Community
import com.example.communityeventmanagement.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCommunityDetail(private val repository: CommunityRepository) {
    operator fun invoke(id: Int): Flow<Community?> = repository.communities.map { communities ->
        communities.find { it.id == id }
    }
}
