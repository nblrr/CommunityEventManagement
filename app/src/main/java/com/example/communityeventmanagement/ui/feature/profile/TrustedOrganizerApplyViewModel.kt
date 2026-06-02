package com.example.communityeventmanagement.ui.feature.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.domain.usecase.community.GetCommunities
import com.example.communityeventmanagement.domain.usecase.user.GetCurrentUser
import com.example.communityeventmanagement.domain.usecase.user.SubmitTrustedApplication
import com.example.communityeventmanagement.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrustedOrganizerApplyViewModel @Inject constructor(
    private val getCurrentUser: GetCurrentUser,
    private val getCommunities: GetCommunities,
    private val submitTrustedApplication: SubmitTrustedApplication,
) : ViewModel() {

    var reason by mutableStateOf("")
    var experience by mutableStateOf("")
    var isSubmitting by mutableStateOf(false)
    var errorMessageResId by mutableStateOf<Int?>(null)

    val isFormValid: Boolean get() = reason.isNotBlank() && experience.isNotBlank()

    fun submit(onSuccess: () -> Unit) {
        val user = getCurrentUser().value ?: return
        if (!isFormValid) return

        viewModelScope.launch {
            isSubmitting = true
            val userCommunities = getCommunities().first().filter { it.organizerId == user.id }
            val communityName = userCommunities.firstOrNull()?.name ?: "Unknown"
            
            when (submitTrustedApplication(communityName, reason.trim(), experience.trim())) {
                is Resource.Success -> {
                    onSuccess()
                }
                is Resource.Error -> {
                    errorMessageResId = com.example.communityeventmanagement.R.string.msg_no_data_found
                }
                is Resource.Loading -> {}
            }
            isSubmitting = false
        }
    }

    fun clearErrors() {
        errorMessageResId = null
    }
}


