package com.example.communityeventmanagement.ui.feature.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.communityeventmanagement.domain.model.User
import com.example.communityeventmanagement.domain.usecase.auth.Login
import com.example.communityeventmanagement.domain.usecase.auth.Register
import com.example.communityeventmanagement.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: Login,
    private val registerUseCase: Register
) : ViewModel() {

    var loginEmail by mutableStateOf("")
    var loginPassword by mutableStateOf("")
    var loginPasswordVisible by mutableStateOf(false)
    var loginErrorMessageResId by mutableStateOf<Int?>(null)
    
    var registerName by mutableStateOf("")
    var registerEmail by mutableStateOf("")
    var registerPassword by mutableStateOf("")
    var registerPasswordVisible by mutableStateOf(false)
    var registerErrorMessageResId by mutableStateOf<Int?>(null)

    val isLoginFormValid: Boolean get() = loginEmail.isNotBlank() && loginPassword.length >= 6
    val isRegisterFormValid: Boolean get() = registerName.isNotBlank() && registerEmail.isNotBlank() && registerPassword.length >= 6

    suspend fun login(onSuccess: (User) -> Unit) {
        when (val result = loginUseCase.invoke(loginEmail, loginPassword)) {
            is Resource.Success -> onSuccess(result.data)
            is Resource.Error -> {
                loginErrorMessageResId = when (result.message) {
                    "INVALID_CREDENTIALS" -> com.example.communityeventmanagement.R.string.error_invalid_credentials
                    "ACCOUNT_BLOCKED" -> com.example.communityeventmanagement.R.string.error_account_blocked
                    else -> com.example.communityeventmanagement.R.string.msg_no_data_found
                }
            }
            is Resource.Loading -> { /* Loading state handled by UI if needed */ }
        }
    }

    suspend fun register(onSuccess: (User) -> Unit) {
        when (val result = registerUseCase.invoke(registerName, registerEmail, registerPassword)) {
            is Resource.Success -> onSuccess(result.data)
            is Resource.Error -> {
                registerErrorMessageResId = when (result.message) {
                    "EMAIL_ALREADY_REGISTERED" -> com.example.communityeventmanagement.R.string.error_email_already_registered
                    else -> com.example.communityeventmanagement.R.string.msg_no_data_found
                }
            }
            is Resource.Loading -> { /* Loading state handled by UI if needed */ }
        }
    }

    fun clearErrors() {
        loginErrorMessageResId = null
        registerErrorMessageResId = null
    }
}


