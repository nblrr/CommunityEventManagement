package com.example.communityeventmanagement.features.organizer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.data.model.OrganizerProfile
import com.example.communityeventmanagement.data.model.UserProfile
import com.example.communityeventmanagement.data.repository.UserRepository
import com.example.communityeventmanagement.util.InputValidation
import kotlinx.coroutines.launch

class OrganizerRegisterViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    var organizerName by mutableStateOf("")
    var personInCharge by mutableStateOf("")
    var description by mutableStateOf("")
    var phone by mutableStateOf("")
    var errorMessage by mutableStateOf<Int?>(null)

    val isFormValid: Boolean
        get() = organizerName.isNotBlank() && personInCharge.isNotBlank() && description.isNotBlank() && phone.isNotBlank()

    fun registerAsOrganizer(onSuccess: (UserProfile) -> Unit) {
        if (!InputValidation.isPhoneValid(phone)) {
            errorMessage = com.example.communityeventmanagement.R.string.error_invalid_phone
            return
        }
        errorMessage = null

        val user = userRepository.currentUser ?: return
        val organizerProfile = OrganizerProfile(
            communityName = organizerName.trim(),
            personInCharge = personInCharge.trim(),
            description = description.trim(),
            phone = phone.trim()
        )
        
        val updatedUser = user.copy(
            role = "Organizer",
            organizerProfile = organizerProfile
        )
        
        val index = userRepository.users.indexOfFirst { it.id == user.id }
        if (index != -1) {
            userRepository.users[index] = updatedUser
            userRepository.currentUser = updatedUser
            viewModelScope.launch {
                userRepository.saveUserData()
                onSuccess(updatedUser)
            }
        }
    }
}
