package com.example.communityeventmanagement.features.community

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.data.model.Community
import com.example.communityeventmanagement.data.repository.CommunityRepository
import com.example.communityeventmanagement.data.repository.UserRepository
import kotlinx.coroutines.launch

class CreateCommunityViewModel(
    private val userRepository: UserRepository,
    private val communityRepository: CommunityRepository
) : ViewModel() {
    var name by mutableStateOf("")
    var category by mutableStateOf("")
    var description by mutableStateOf("")
    var coverImageUri by mutableStateOf<String?>(null)

    val isFormValid: Boolean
        get() = name.isNotBlank() && category.isNotBlank() && description.isNotBlank()

    fun createCommunity(onSuccess: (Int) -> Unit) {
        val user = userRepository.currentUser ?: return
        val newId = (communityRepository.communities.maxOfOrNull { it.id } ?: 0) + 1
        val newCommunity = Community(
            id = newId,
            name = name.trim(),
            category = category.trim(),
            description = description.trim(),
            coverImageUri = coverImageUri,
            organizerId = user.id,
            organizerName = user.name
        )
        communityRepository.communities.add(newCommunity)
        viewModelScope.launch {
            communityRepository.saveCommunityData()
            communityRepository.joinedCommunityIds.add(newId)
            onSuccess(newId)
        }
    }
}
