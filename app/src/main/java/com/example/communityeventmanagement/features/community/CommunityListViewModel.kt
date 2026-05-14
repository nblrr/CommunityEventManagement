package com.example.communityeventmanagement.features.community

import androidx.lifecycle.ViewModel
import com.example.communityeventmanagement.data.repository.CommunityRepository

class CommunityListViewModel(
    private val communityRepository: CommunityRepository
) : ViewModel() {
    val communities
        get() = communityRepository.communities
        
    val joinedCommunityIds
        get() = communityRepository.joinedCommunityIds
}
