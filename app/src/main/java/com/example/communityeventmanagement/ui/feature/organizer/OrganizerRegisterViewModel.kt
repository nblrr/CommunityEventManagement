package com.example.communityeventmanagement.ui.feature.organizer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.domain.model.Organizer
import com.example.communityeventmanagement.domain.usecase.user.GetCurrentUser
import com.example.communityeventmanagement.domain.usecase.user.RegisterOrganizer
import com.example.communityeventmanagement.util.Resource
import com.example.communityeventmanagement.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrganizerRegisterViewModel @Inject constructor(
    private val getCurrentUser: GetCurrentUser,
    private val registerOrganizer: RegisterOrganizer
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var errorMessageResId by mutableStateOf<Int?>(null)

    fun register(organizerName: String, personInCharge: String, contact: String, description: String, onSuccess: () -> Unit) {
        val user = getCurrentUser().value ?: return
        viewModelScope.launch {
            val organizer = Organizer(
                communityName = organizerName,
                personInCharge = personInCharge,
                phone = contact,
                description = description
            )
            when (registerOrganizer.invoke(user.id, organizer)) {
                is Resource.Success -> onSuccess()
                is Resource.Error -> {
                    errorMessageResId = com.example.communityeventmanagement.R.string.msg_no_data_found
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun clearErrors() {
        errorMessageResId = null
    }
}


