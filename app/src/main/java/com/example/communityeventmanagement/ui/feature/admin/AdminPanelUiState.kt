package com.example.communityeventmanagement.ui.feature.admin

import com.example.communityeventmanagement.domain.entities.TrustedApplication
import com.example.communityeventmanagement.domain.entities.User

data class AdminPanelUiState(
    val selectedTab: Int = 0,
    val searchQuery: String = "",
    val users: List<User> = emptyList(),
    val pendingApplications: List<TrustedApplication> = emptyList(),
    val userToToggleBlock: User? = null,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val messageArg: String? = null
)
