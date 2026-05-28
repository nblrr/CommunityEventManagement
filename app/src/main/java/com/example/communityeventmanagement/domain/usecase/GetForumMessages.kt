package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.entities.ForumMessage
import com.example.communityeventmanagement.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetForumMessages @Inject constructor(private val repository: CommunityRepository) {
    operator fun invoke(communityId: Int): Flow<List<ForumMessage>> = repository.communities.map { communities ->
        communities.find { it.id == communityId }?.forumMessages ?: emptyList()
    }
}
