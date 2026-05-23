package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.entities.ForumMessage
import com.example.communityeventmanagement.domain.repository.CommunityRepository

class SendMessage(private val repository: CommunityRepository) {
    suspend operator fun invoke(communityId: Int, message: ForumMessage): Result<Unit> = repository.addForumMessage(communityId, message)
}
