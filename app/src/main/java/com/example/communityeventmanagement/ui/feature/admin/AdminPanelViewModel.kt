package com.example.communityeventmanagement.ui.feature.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.domain.model.User
import com.example.communityeventmanagement.domain.usecase.admin.AddAdmin
import com.example.communityeventmanagement.domain.usecase.admin.ApproveTrustedApplication
import com.example.communityeventmanagement.domain.usecase.admin.GetPendingTrustedApplications
import com.example.communityeventmanagement.domain.usecase.admin.GetUsers
import com.example.communityeventmanagement.domain.usecase.admin.RejectTrustedApplication
import com.example.communityeventmanagement.domain.usecase.admin.ToggleUserBlock
import com.example.communityeventmanagement.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminPanelViewModel @Inject constructor(
    getUsers: GetUsers,
    getPendingTrustedApplications: GetPendingTrustedApplications,
    private val approveTrustedApplication: ApproveTrustedApplication,
    private val rejectTrustedApplication: RejectTrustedApplication,
    private val toggleUserBlock: ToggleUserBlock,
    private val addAdmin: AddAdmin
) : ViewModel() {

    var searchQuery by mutableStateOf("")
    var selectedTab by mutableIntStateOf(0)
    var userToToggleBlock by mutableStateOf<User?>(null)
    var userMessage by mutableStateOf<String?>(null)

    var showAddAdminDialog by mutableStateOf(false)
    var newAdminName by mutableStateOf("")
    var newAdminEmail by mutableStateOf("")
    var newAdminPassword by mutableStateOf("")
    var isAddingAdmin by mutableStateOf(false)

    val users = getUsers().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val pendingApplications = getPendingTrustedApplications().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun approveApplication(userId: String) {
        viewModelScope.launch {
            approveTrustedApplication.invoke(userId)
            userMessage = "Application approved"
        }
    }

    fun rejectApplication(userId: String) {
        viewModelScope.launch {
            rejectTrustedApplication.invoke(userId)
            userMessage = "Application rejected"
        }
    }

    fun toggleUserBlock(userId: String) {
        viewModelScope.launch {
            val result = toggleUserBlock.invoke(userId)
            if (result is Resource.Error) {
                userMessage = result.message
            }
            userToToggleBlock = null
        }
    }

    fun onAddAdmin() {
        if (newAdminName.isBlank() || newAdminEmail.isBlank() || newAdminPassword.isBlank()) {
            userMessage = "Please fill all fields"
            return
        }
        viewModelScope.launch {
            isAddingAdmin = true
            val result = addAdmin(newAdminName, newAdminEmail, newAdminPassword)
            isAddingAdmin = false
            when (result) {
                is Resource.Success -> {
                    userMessage = "Admin added successfully"
                    showAddAdminDialog = false
                    resetAddAdminFields()
                }
                is Resource.Error -> {
                    userMessage = result.message
                }
                else -> {}
            }
        }
    }

    private fun resetAddAdminFields() {
        newAdminName = ""
        newAdminEmail = ""
        newAdminPassword = ""
    }

    fun clearMessage() {
        userMessage = null
    }
}


