package com.example.communityeventmanagement.features.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.data.model.UserProfile
import com.example.communityeventmanagement.data.repository.CommunityRepository
import com.example.communityeventmanagement.data.repository.LoginResult
import com.example.communityeventmanagement.data.repository.UserRepository
import com.example.communityeventmanagement.util.InputValidation
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userRepository: UserRepository,
    private val communityRepository: CommunityRepository,
) : ViewModel() {
    // Login state
    var loginEmail by mutableStateOf("")
    var loginPassword by mutableStateOf("")
    var loginPasswordVisible by mutableStateOf(value = false)
    var loginErrorMessage by mutableStateOf<String?>(null)

    val isLoginFormValid: Boolean
        get() = loginEmail.isNotBlank() && loginPassword.isNotBlank()

    // Register state
    var registerName by mutableStateOf("")
    var registerEmail by mutableStateOf("")
    var registerPassword by mutableStateOf("")
    var registerPasswordVisible by mutableStateOf(value = false)
    var registerErrorMessage by mutableStateOf<String?>(null)

    val isRegisterFormValid: Boolean
        get() = registerName.isNotBlank() && registerEmail.isNotBlank() && registerPassword.isNotBlank()

    fun login(onSuccess: (UserProfile) -> Unit, context: android.content.Context) {
        viewModelScope.launch {
            when (val result = userRepository.loginWithCredentials(loginEmail, loginPassword)) {
                is LoginResult.Success -> {
                    communityRepository.refreshUserParticipation(result.user)
                    onSuccess(result.user)
                }
                is LoginResult.Error -> loginErrorMessage = result.message
                is LoginResult.ErrorResource -> loginErrorMessage = context.getString(result.resId)
            }
        }
    }

    fun register(onSuccess: (UserProfile) -> Unit, context: android.content.Context) {
        val trimmedEmail = registerEmail.trim()
        val validation = InputValidation.validateRegisterForm(registerName, trimmedEmail, registerPassword, registerPassword)
        
        if (validation is InputValidation.ValidationResult.Invalid) {
            registerErrorMessage = context.getString(validation.messageRes)
            return
        }
        
        if (userRepository.users.any { it.email.equals(trimmedEmail, ignoreCase = true) }) {
            registerErrorMessage = context.getString(com.example.communityeventmanagement.R.string.error_email_already_registered)
            return
        }

        viewModelScope.launch {
            val newUser = UserProfile(
                id = "user_${System.currentTimeMillis()}",
                name = registerName.trim(),
                email = trimmedEmail,
                password = registerPassword
            )
            userRepository.users.add(newUser)
            userRepository.saveUserData()
            userRepository.login(newUser)
            communityRepository.refreshUserParticipation(newUser)
            onSuccess(newUser)
        }
    }

    fun clearErrors() {
        loginErrorMessage = null
        registerErrorMessage = null
    }
}
