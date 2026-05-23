package com.example.communityeventmanagement.ui.feature.organizer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.domain.entities.Organizer
import com.example.communityeventmanagement.domain.usecase.GetCurrentUser
import com.example.communityeventmanagement.domain.usecase.RegisterOrganizer
import kotlinx.coroutines.launch

class OrganizerRegisterViewModel(
    private val getCurrentUser: GetCurrentUser,
    private val registerOrganizer: RegisterOrganizer
) : ViewModel() {

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
            val result = registerOrganizer.invoke(user.id, organizer)
            if (result.isSuccess) {
                onSuccess()
            } else {
                errorMessageResId = com.example.communityeventmanagement.R.string.msg_no_data_found
            }
        }
    }

    fun clearErrors() {
        errorMessageResId = null
    }
}
