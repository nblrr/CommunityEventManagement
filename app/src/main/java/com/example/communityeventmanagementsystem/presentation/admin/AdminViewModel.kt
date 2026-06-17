package com.example.communityeventmanagementsystem.presentation.admin

import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.usecase.admin.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val getDashboardStatsUseCase: GetDashboardStatsUseCase,
    private val getUsersUseCase: GetUsersUseCase,
    private val getAdminTrustedAppsUseCase: GetAdminTrustedAppsUseCase,
    private val approveTrustedAppUseCase: ApproveTrustedAppUseCase,
    private val rejectTrustedAppUseCase: RejectTrustedAppUseCase,
    private val blockUserUseCase: BlockUserUseCase,
    private val unblockUserUseCase: UnblockUserUseCase
) : BaseViewModel<AdminContract.State, AdminContract.Event, AdminContract.Effect>() {

    override fun createInitialState(): AdminContract.State = AdminContract.State()

    override fun handleEvent(event: AdminContract.Event) {
        when (event) {
            is AdminContract.Event.LoadDashboard -> loadDashboard(forceRefresh = false)
            is AdminContract.Event.OnRefresh -> loadDashboard(forceRefresh = true)
            is AdminContract.Event.OnTabSelected -> setState { copy(selectedTab = event.index) }
            is AdminContract.Event.OnApproveApp -> approveApp(event.id)
            is AdminContract.Event.OnShowRejectDialog -> setState { copy(showRejectDialog = true, rejectTargetAppId = event.id, rejectNotes = "") }
            is AdminContract.Event.OnDismissRejectDialog -> setState { copy(showRejectDialog = false, rejectTargetAppId = null, rejectNotes = "") }
            is AdminContract.Event.OnRejectNotesChanged -> setState { copy(rejectNotes = event.notes) }
            is AdminContract.Event.OnConfirmReject -> {
                val appId = uiState.value.rejectTargetAppId
                val notes = uiState.value.rejectNotes.ifBlank { "Rejected by Admin" }
                if (appId != null) {
                    setState { copy(showRejectDialog = false, rejectTargetAppId = null, rejectNotes = "") }
                    rejectApp(appId, notes)
                }
            }
            is AdminContract.Event.OnBlockUser -> blockUser(event.id)
            is AdminContract.Event.OnUnblockUser -> unblockUser(event.id)
        }
    }

    private fun loadDashboard(forceRefresh: Boolean) {
        if (!forceRefresh && uiState.value.stats != null && !uiState.value.isLoading) return
        viewModelScope.launch {
            setState {
                copy(
                    isLoading = stats == null,
                    isRefreshing = stats != null,
                    error = null
                )
            }
            val statsDeferred = async { getDashboardStatsUseCase() }
            val usersDeferred = async { getUsersUseCase() }
            val appsDeferred = async { getAdminTrustedAppsUseCase() }

            val statsRes = statsDeferred.await()
            val usersRes = usersDeferred.await()
            val appsRes = appsDeferred.await()

            setState {
                copy(
                    isLoading = false,
                    isRefreshing = false,
                    stats = if (statsRes is NetworkResult.Success) statsRes.data else stats,
                    users = if (usersRes is NetworkResult.Success) usersRes.data else users,
                    pendingApps = if (appsRes is NetworkResult.Success) appsRes.data.filter { it.status == "pending" } else pendingApps,
                    error = (statsRes as? NetworkResult.Error)?.message
                )
            }
        }
    }

    private fun approveApp(id: Long) {
        viewModelScope.launch {
            when (val result = approveTrustedAppUseCase(id)) {
                is NetworkResult.Success -> {
                    setEffect { AdminContract.Effect.ShowSnackbar("Application approved successfully") }
                    loadDashboard(forceRefresh = true)
                }
                is NetworkResult.Error -> {
                    setEffect { AdminContract.Effect.ShowSnackbar("Failed: ${result.message}") }
                }
                else -> {}
            }
        }
    }

    private fun rejectApp(id: Long, notes: String) {
        viewModelScope.launch {
            when (val result = rejectTrustedAppUseCase(id, notes)) {
                is NetworkResult.Success -> {
                    setEffect { AdminContract.Effect.ShowSnackbar("Application rejected") }
                    loadDashboard(forceRefresh = true)
                }
                is NetworkResult.Error -> {
                    setEffect { AdminContract.Effect.ShowSnackbar("Failed: ${result.message}") }
                }
                else -> {}
            }
        }
    }

    private fun blockUser(id: Long) {
        viewModelScope.launch {
            when (val result = blockUserUseCase(id)) {
                is NetworkResult.Success -> {
                    setEffect { AdminContract.Effect.ShowSnackbar("User blocked") }
                    loadDashboard(forceRefresh = true)
                }
                is NetworkResult.Error -> {
                    setEffect { AdminContract.Effect.ShowSnackbar("Failed: ${result.message}") }
                }
                else -> {}
            }
        }
    }

    private fun unblockUser(id: Long) {
        viewModelScope.launch {
            when (val result = unblockUserUseCase(id)) {
                is NetworkResult.Success -> {
                    setEffect { AdminContract.Effect.ShowSnackbar("User unblocked") }
                    loadDashboard(forceRefresh = true)
                }
                is NetworkResult.Error -> {
                    setEffect { AdminContract.Effect.ShowSnackbar("Failed: ${result.message}") }
                }
                else -> {}
            }
        }
    }
}
