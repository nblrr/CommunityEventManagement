package com.example.communityeventmanagement.ui.feature.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.communityeventmanagement.domain.entities.User
import com.example.communityeventmanagement.domain.usecase.Login
import com.example.communityeventmanagement.domain.usecase.Register

class AuthViewModel(
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
        val result = loginUseCase.invoke(loginEmail, loginPassword)
        if (result.isSuccess) {
            onSuccess(result.getOrThrow())
        } else {
            loginErrorMessageResId = when (result.exceptionOrNull()?.message) {
                "INVALID_CREDENTIALS" -> com.example.communityeventmanagement.R.string.error_invalid_credentials
                "ACCOUNT_BLOCKED" -> com.example.communityeventmanagement.R.string.error_account_blocked
                else -> com.example.communityeventmanagement.R.string.msg_no_data_found
            }
        }
    }

    suspend fun register(onSuccess: (User) -> Unit) {
        val result = registerUseCase.invoke(registerName, registerEmail, registerPassword)
        if (result.isSuccess) {
            onSuccess(result.getOrThrow())
        } else {
            registerErrorMessageResId = when (result.exceptionOrNull()?.message) {
                "EMAIL_ALREADY_REGISTERED" -> com.example.communityeventmanagement.R.string.error_email_already_registered
                else -> com.example.communityeventmanagement.R.string.msg_no_data_found
            }
        }
    }

    fun clearErrors() {
        loginErrorMessageResId = null
        registerErrorMessageResId = null
    }
}
