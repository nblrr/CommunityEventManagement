package com.example.communityeventmanagement.ui.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.domain.entities.ThemeMode
import com.example.communityeventmanagement.domain.entities.User
import com.example.communityeventmanagement.domain.usecase.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getCurrentUser: GetCurrentUser,
    private val getCommunities: GetCommunities,
    private val updateAvatar: UpdateAvatar,
    private val updateProfile: UpdateProfile,
    private val submitTrustedApplication: SubmitTrustedApplication,
    private val saveTheme: SaveTheme,
    private val logout: Logout,
    val themeModeFlow: StateFlow<ThemeMode>
) : ViewModel() {

    val currentUser: StateFlow<User?> = getCurrentUser().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
    val communities = getCommunities()

    fun updateAvatar(uri: String?) {
        viewModelScope.launch {
            updateAvatar.invoke(uri)
        }
    }

    fun submitTrustedApplication(organizerName: String, reason: String) {
        viewModelScope.launch {
            val userCommunities = communities.first().filter { it.organizerName == organizerName }
            submitTrustedApplication.invoke(userCommunities, reason, "Experienced organizer")
        }
    }

    fun saveTheme(themeMode: Int) {
        viewModelScope.launch {
            val mode = when(themeMode) {
                1 -> ThemeMode.LIGHT
                2 -> ThemeMode.DARK
                else -> ThemeMode.AUTO
            }
            saveTheme.invoke(mode)
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            logout.invoke()
            onSuccess()
        }
    }

    fun updateProfile(name: String, bio: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            updateProfile.invoke(name, bio).onSuccess {
                onSuccess()
            }
        }
    }
}
