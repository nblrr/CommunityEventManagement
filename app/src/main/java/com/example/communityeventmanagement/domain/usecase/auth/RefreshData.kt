package com.example.communityeventmanagement.domain.usecase.auth

import com.example.communityeventmanagement.domain.model.User
import com.example.communityeventmanagement.domain.repository.CommunityRepository
import javax.inject.Inject

class RefreshData @Inject constructor(private val repository: CommunityRepository) {
    suspend operator fun invoke(currentUser: User?) {
        repository.refreshUserParticipation(currentUser)
    }
}

