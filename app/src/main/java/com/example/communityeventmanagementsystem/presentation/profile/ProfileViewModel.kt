package com.example.communityeventmanagementsystem.presentation.profile

import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.repository.AuthRepository
import com.example.communityeventmanagementsystem.domain.usecase.profile.GetProfileUseCase
import com.example.communityeventmanagementsystem.domain.usecase.profile.UpdateProfileUseCase
import com.example.communityeventmanagementsystem.domain.usecase.profile.UploadAvatarUseCase
import com.example.communityeventmanagementsystem.domain.usecase.profile.BecomeOrganizerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val uploadAvatarUseCase: UploadAvatarUseCase,
    private val becomeOrganizerUseCase: BecomeOrganizerUseCase,
    private val authRepository: AuthRepository
) : BaseViewModel<ProfileContract.State, ProfileContract.Event, ProfileContract.Effect>() {

    override fun createInitialState(): ProfileContract.State = ProfileContract.State()

    override fun handleEvent(event: ProfileContract.Event) {
        when (event) {
            is ProfileContract.Event.LoadProfile -> loadProfile()
            is ProfileContract.Event.UpdateProfile -> updateProfile(event.user)
            is ProfileContract.Event.UploadAvatar -> uploadAvatar(event.file)
            is ProfileContract.Event.BecomeOrganizer -> becomeOrganizer()
            is ProfileContract.Event.Logout -> logout()
        }
    }

    private fun loadProfile() {
        if (uiState.value.isLoading) return
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null, errorCode = null, isSessionExpired = false) }
            when (val result = getProfileUseCase()) {
                is NetworkResult.Success -> setState { copy(isLoading = false, user = result.data) }
                is NetworkResult.Error -> setState { copy(isLoading = false, error = result.message, errorCode = result.code, isSessionExpired = result.code == 401) }
                is NetworkResult.Loading -> setState { copy(isLoading = true) }
            }
        }
    }

    private fun updateProfile(user: com.example.communityeventmanagementsystem.domain.model.User) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null, errorCode = null, isSessionExpired = false) }
            when (val result = updateProfileUseCase(user)) {
                is NetworkResult.Success -> {
                    setState { copy(isLoading = false, user = result.data) }
                    setEffect { ProfileContract.Effect.ProfileUpdated }
                }
                is NetworkResult.Error -> setState { copy(isLoading = false, error = result.message, errorCode = result.code, isSessionExpired = result.code == 401) }
                is NetworkResult.Loading -> setState { copy(isLoading = true) }
            }
        }
    }

    private fun uploadAvatar(file: java.io.File) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null, errorCode = null, isSessionExpired = false) }
            when (val result = uploadAvatarUseCase(file)) {
                is NetworkResult.Success -> {
                    setState { copy(isLoading = false, user = result.data) }
                    // No Effect needed, ProfileScreen will observe state change
                }
                is NetworkResult.Error -> setState { copy(isLoading = false, error = result.message, errorCode = result.code, isSessionExpired = result.code == 401) }
                is NetworkResult.Loading -> setState { copy(isLoading = true) }
            }
        }
    }

    private fun becomeOrganizer() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null, errorCode = null, isSessionExpired = false) }
            when (val result = becomeOrganizerUseCase()) {
                is NetworkResult.Success -> setState { copy(isLoading = false, user = result.data) }
                is NetworkResult.Error -> setState { copy(isLoading = false, error = result.message, errorCode = result.code, isSessionExpired = result.code == 401) }
                is NetworkResult.Loading -> setState { copy(isLoading = true) }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            setEffect { ProfileContract.Effect.NavigateToLogin }
        }
    }
}
