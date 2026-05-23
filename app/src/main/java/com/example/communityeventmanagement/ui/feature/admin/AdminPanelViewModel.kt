package com.example.communityeventmanagement.ui.feature.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.domain.entities.User
import com.example.communityeventmanagement.domain.usecase.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminPanelViewModel(
    getUsers: GetUsers,
    getPendingApplications: GetPendingApplications,
    private val approveApplication: ApproveApplication,
    private val rejectApplication: RejectApplication,
    private val toggleUserBlock: ToggleUserBlock
) : ViewModel() {

    var searchQuery by mutableStateOf("")
    var selectedTab by mutableStateOf(0)
    var userToToggleBlock by mutableStateOf<User?>(null)
    var userMessage by mutableStateOf<String?>(null)

    val users = getUsers().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val pendingApplications = getPendingApplications().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun approveApplication(applicationId: String) {
        viewModelScope.launch {
            approveApplication.invoke(applicationId)
        }
    }

    fun rejectApplication(applicationId: String) {
        viewModelScope.launch {
            rejectApplication.invoke(applicationId)
        }
    }

    fun toggleUserBlock(userId: String) {
        viewModelScope.launch {
            toggleUserBlock.invoke(userId)
            userToToggleBlock = null
        }
    }

    fun clearMessage() {
        userMessage = null
    }
}
