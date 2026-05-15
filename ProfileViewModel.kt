package com.example.communityeventmanagement.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.data.repository.CommunityRepository
import com.example.communityeventmanagement.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val communityRepository: CommunityRepository,
) : ViewModel() {
    val joinedCommunities
        get() = communityRepository.communities.filter { it.id in communityRepository.joinedCommunityIds }
        
    val managedCommunities
        get() = communityRepository.communities.filter { it.organizerId == userRepository.currentUser?.id }

    val currentThemeMode get() = userRepository.themeMode

    fun saveTheme(mode: String) {
        viewModelScope.launch {
            userRepository.saveTheme(mode)
        }
    }

    fun submitTrustedApplication(reason: String, experience: String) {
        viewModelScope.launch {
            userRepository.submitTrustedApplication(communityRepository.communities, reason, experience)
        }
    }

    fun updateAvatar(newUri: String?) {
        viewModelScope.launch {
            userRepository.updateAvatar(newUri)
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            communityRepository.joinedCommunityIds.clear()
            communityRepository.registeredEventIds.clear()
        }
    }
}
