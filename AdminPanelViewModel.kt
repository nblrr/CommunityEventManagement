package com.example.communityeventmanagement.features.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.data.model.UserProfile
import com.example.communityeventmanagement.data.repository.UserRepository
import kotlinx.coroutines.launch

class AdminPanelViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {
    var selectedTab by mutableIntStateOf(0)
    var searchQuery by mutableStateOf("")
    var userToToggleBlock by mutableStateOf<UserProfile?>(null)

    val users: List<UserProfile>
        get() = userRepository.users.asSequence().filter { user ->
            (if (selectedTab == 1) user.role == "Organizer" else true) &&
                    (user.name.contains(searchQuery, ignoreCase = true) || user.email.contains(searchQuery, ignoreCase = true))
        }.sortedBy { it.role }.toList()

    val pendingApplications
        get() = userRepository.trustedApplications

    fun approveApplication(userId: String, onComplete: (String) -> Unit) {
        val application = pendingApplications.find { it.userId == userId }
        viewModelScope.launch {
            userRepository.handleTrustedApplication(userId, approve = true)
            onComplete("Pengajuan ${application?.userName ?: ""} disetujui.")
        }
    }

    fun rejectApplication(userId: String, onComplete: (String) -> Unit) {
        val application = pendingApplications.find { it.userId == userId }
        viewModelScope.launch {
            userRepository.handleTrustedApplication(userId, approve = false)
            onComplete("Pengajuan ${application?.userName ?: ""} ditolak.")
        }
    }

    fun toggleBlock(user: UserProfile, onComplete: (String) -> Unit) {
        val index = userRepository.users.indexOfFirst { it.id == user.id }
        if (index != -1) {
            val wasBlocked = user.isBlocked
            val updatedUser = user.copy(isBlocked = !wasBlocked)
            userRepository.users[index] = updatedUser
            
            if (userRepository.currentUser?.id == user.id) {
                userRepository.currentUser = updatedUser
            }

            viewModelScope.launch {
                userRepository.saveUserData()
                onComplete(
                    if (wasBlocked) "${user.name} berhasil dibuka blokirnya."
                    else "${user.name} berhasil diblokir."
                )
            }
        }
    }
}
