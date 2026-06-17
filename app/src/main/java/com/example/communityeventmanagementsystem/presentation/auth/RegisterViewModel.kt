package com.example.communityeventmanagementsystem.presentation.auth

import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.data.remote.dto.RegisterRequest
import com.example.communityeventmanagementsystem.domain.usecase.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : BaseViewModel<RegisterContract.State, RegisterContract.Event, RegisterContract.Effect>() {

    override fun createInitialState(): RegisterContract.State {
        return RegisterContract.State()
    }

    override fun handleEvent(event: RegisterContract.Event) {
        when (event) {
            is RegisterContract.Event.OnRegisterClicked -> {
                register(event.name, event.email, event.pass, event.passConfirm)
            }
        }
    }

    private fun register(name: String, email: String, pass: String, passConfirm: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            val request = RegisterRequest(name, email, pass, passConfirm)
            when (val result = registerUseCase(request)) {
                is NetworkResult.Success -> {
                    setState { copy(isLoading = false, user = result.data) }
                    setEffect { RegisterContract.Effect.NavigationToHome }
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
