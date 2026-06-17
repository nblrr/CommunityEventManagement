package com.example.communityeventmanagementsystem.presentation.admin

import com.example.communityeventmanagementsystem.domain.model.DashboardStats
import com.example.communityeventmanagementsystem.domain.model.TrustedApplication
import com.example.communityeventmanagementsystem.domain.model.User

class AdminContract {
    data class State(
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val stats: DashboardStats? = null,
        val users: List<User> = emptyList(),
        val pendingApps: List<TrustedApplication> = emptyList(),
        val selectedTab: Int = 0,
        val showRejectDialog: Boolean = false,
        val rejectTargetAppId: Long? = null,
        val rejectNotes: String = "",
        val error: String? = null
    )

    sealed class Event {
        object LoadDashboard : Event()
        object OnRefresh : Event()
        data class OnTabSelected(val index: Int) : Event()
        data class OnApproveApp(val id: Long) : Event()
        data class OnShowRejectDialog(val id: Long) : Event()
        object OnDismissRejectDialog : Event()
        data class OnRejectNotesChanged(val notes: String) : Event()
        object OnConfirmReject : Event()
        data class OnBlockUser(val id: Long) : Event()
        data class OnUnblockUser(val id: Long) : Event()
    }

    sealed class Effect {
        data class ShowSnackbar(val message: String) : Effect()
    }
}
