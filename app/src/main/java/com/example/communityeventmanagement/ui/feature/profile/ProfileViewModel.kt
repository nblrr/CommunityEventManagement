package com.example.communityeventmanagement.ui.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.domain.model.ThemeMode
import com.example.communityeventmanagement.domain.model.User
import com.example.communityeventmanagement.domain.repository.UserRepository
import com.example.communityeventmanagement.domain.usecase.app.SaveTheme
import com.example.communityeventmanagement.domain.usecase.auth.Logout
import com.example.communityeventmanagement.domain.usecase.community.GetCommunities
import com.example.communityeventmanagement.domain.usecase.user.GetCurrentUser
import com.example.communityeventmanagement.domain.usecase.user.UpdateAvatar
import com.example.communityeventmanagement.domain.usecase.user.UpdateProfile
import com.example.communityeventmanagement.util.Resource
import com.example.communityeventmanagement.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    getCurrentUser: GetCurrentUser,
    getCommunities: GetCommunities,
    private val updateAvatar: UpdateAvatar,
    private val updateProfile: UpdateProfile,
    private val saveTheme: SaveTheme,
    private val logout: Logout,
    userRepository: UserRepository
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    val themeModeFlow: StateFlow<ThemeMode> = userRepository.themeMode

    val currentUser: StateFlow<User?> = getCurrentUser().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
    val communities = getCommunities()

    fun updateAvatar(uri: String?) {
        viewModelScope.launch {
            when (val result = updateAvatar.invoke(uri)) {
                is Resource.Success -> _uiEvent.send(UiEvent.ShowSnackbar("Avatar berhasil diperbarui"))
                is Resource.Error -> _uiEvent.send(UiEvent.ShowSnackbar(result.message))
                is Resource.Loading -> {}
            }
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

