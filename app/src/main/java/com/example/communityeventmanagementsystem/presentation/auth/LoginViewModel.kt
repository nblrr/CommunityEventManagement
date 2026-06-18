package com.example.communityeventmanagementsystem.presentation.auth

import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.data.remote.dto.LoginRequest
import com.example.communityeventmanagementsystem.domain.usecase.auth.LoginUseCase
import com.example.communityeventmanagementsystem.domain.repository.NotificationRepository
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val notificationRepository: NotificationRepository
) : BaseViewModel<LoginContract.State, LoginContract.Event, LoginContract.Effect>() {

    override fun createInitialState(): LoginContract.State {
        return LoginContract.State()
    }

    override fun handleEvent(event: LoginContract.Event) {
        when (event) {
            is LoginContract.Event.OnLoginClicked -> {
                login(event.email, event.pass)
            }
        }
    }

    private fun login(email: String, pass: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            val request = LoginRequest(email, pass)
            when (val result = loginUseCase(request)) {
                is NetworkResult.Success -> {
                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            viewModelScope.launch {
                                notificationRepository.updateFcmToken(task.result)
                            }
                        }
                    }
                    setState { copy(isLoading = false, user = result.data) }
                    setEffect { LoginContract.Effect.NavigationToHome }
                }
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false, error = result.message) }
                }
                is NetworkResult.Loading -> {
                    setState { copy(isLoading = true) }
                }
            }
        }
    }
}
