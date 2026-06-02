package com.example.communityeventmanagement.domain.usecase.forum

import com.example.communityeventmanagement.domain.model.ForumMessage
import com.example.communityeventmanagement.domain.repository.CommunityRepository
import com.example.communityeventmanagement.util.Resource
import javax.inject.Inject

class SendMessage @Inject constructor(private val repository: CommunityRepository) {
    suspend operator fun invoke(communityId: Int, message: ForumMessage): Resource<Unit> = repository.addForumMessage(communityId, message)
}

