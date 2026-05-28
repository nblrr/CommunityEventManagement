package com.example.communityeventmanagement.ui.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.domain.entities.ThemeMode
import com.example.communityeventmanagement.domain.entities.User
import com.example.communityeventmanagement.domain.usecase.*
import com.example.communityeventmanagement.domain.repository.UserRepository
import com.example.communityeventmanagement.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUser: GetCurrentUser,
    private val getCommunities: GetCommunities,
    private val updateAvatar: UpdateAvatar,
    private val updateProfile: UpdateProfile,
    private val saveTheme: SaveTheme,
    private val logout: Logout,
    private val userRepository: UserRepository
) : ViewModel() {

    val themeModeFlow: StateFlow<ThemeMode> = userRepository.themeMode

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
            when (updateProfile.invoke(name, bio)) {
                is Resource.Success -> onSuccess()
                is Resource.Error -> { /* Handle error */ }
                is Resource.Loading -> {}
            }
        }
    }
}
