package com.example.communityeventmanagement.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.domain.entities.User
import com.example.communityeventmanagement.domain.repository.UserRepository
import com.example.communityeventmanagement.domain.usecase.InitializeApp
import com.example.communityeventmanagement.domain.usecase.Logout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val initializeApp: InitializeApp,
    private val logoutUseCase: Logout
) : ViewModel() {

    val currentUser: StateFlow<User?> = userRepository.currentUser
    val themeMode = userRepository.themeMode

    suspend fun initialize() {
        initializeApp()
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}
