package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.entities.ForumMessage
import com.example.communityeventmanagement.domain.repository.CommunityRepository
import com.example.communityeventmanagement.domain.util.Resource
import javax.inject.Inject

class SendMessage @Inject constructor(private val repository: CommunityRepository) {
    suspend operator fun invoke(communityId: Int, message: ForumMessage): Resource<Unit> = repository.addForumMessage(communityId, message)
}
