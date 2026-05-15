package com.example.communityeventmanagement.features.community

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import com.example.communityeventmanagement.data.repository.CommunityRepository

class CommunityListViewModel(
    private val communityRepository: CommunityRepository
) : ViewModel() {
    val communities by derivedStateOf {
        communityRepository.communities
    }
        
    val joinedCommunityIds by derivedStateOf {
        communityRepository.joinedCommunityIds
    }
}
