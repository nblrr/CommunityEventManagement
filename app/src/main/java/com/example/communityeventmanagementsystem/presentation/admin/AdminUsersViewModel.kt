package com.example.communityeventmanagementsystem.presentation.admin

import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.model.User
import com.example.communityeventmanagementsystem.domain.usecase.admin.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class AdminUsersContract {
    data class State(
        val isLoading: Boolean = false,
        val users: List<User> = emptyList(),
        val searchQuery: String = "",
        val selectedRole: String = "ALL",
        val selectedStatus: String = "ALL",
        val error: String? = null,
        val showCreateDialog: Boolean = false,
        val createName: String = "",
        val createEmail: String = "",
        val createRole: String = "USER",
        val createPassword: String = "",
        val showRoleDialog: Boolean = false,
        val roleTargetUser: User? = null,
        val roleSelectedValue: String = "USER",
        val selectedUserDetail: User? = null
    )

    sealed class Event {
        object LoadUsers : Event()
        data class OnSearchQueryChanged(val query: String) : Event()
        data class OnRoleFilterChanged(val role: String) : Event()
        data class OnStatusFilterChanged(val status: String) : Event()
        data class OnBlockUser(val id: Long) : Event()
        data class OnUnblockUser(val id: Long) : Event()
        data class OnDeleteUser(val id: Long) : Event()
        data class OnRevokeTrusted(val id: Long) : Event()
        data class OnShowCreateDialog(val show: Boolean) : Event()
        data class OnCreateFieldChanged(val field: String, val value: String) : Event()
        object OnCreateUser : Event()
        data class OnShowRoleDialog(val show: Boolean, val user: User? = null) : Event()
        data class OnRoleSelectedValueChange(val role: String) : Event()
        object OnConfirmRoleChange : Event()
        data class OnShowUserDetail(val user: User?) : Event()
    }

    sealed class Effect {
        data class ShowSnackbar(val message: String) : Effect()
    }
}

@HiltViewModel
class AdminUsersViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val updateUserRoleUseCase: UpdateUserRoleUseCase,
    private val revokeTrustedUseCase: RevokeTrustedUseCase,
    private val blockUserUseCase: BlockUserUseCase,
    private val unblockUserUseCase: UnblockUserUseCase
) : BaseViewModel<AdminUsersContract.State, AdminUsersContract.Event, AdminUsersContract.Effect>() {

    override fun createInitialState(): AdminUsersContract.State = AdminUsersContract.State()

    override fun handleEvent(event: AdminUsersContract.Event) {
        when (event) {
            is AdminUsersContract.Event.LoadUsers -> loadUsers()
            is AdminUsersContract.Event.OnSearchQueryChanged -> {
                setState { copy(searchQuery = event.query) }
                loadUsers()
            }
            is AdminUsersContract.Event.OnRoleFilterChanged -> {
                setState { copy(selectedRole = event.role) }
                loadUsers()
            }
            is AdminUsersContract.Event.OnStatusFilterChanged -> {
                setState { copy(selectedStatus = event.status) }
                loadUsers()
            }
            is AdminUsersContract.Event.OnBlockUser -> blockUser(event.id)
            is AdminUsersContract.Event.OnUnblockUser -> unblockUser(event.id)
            is AdminUsersContract.Event.OnDeleteUser -> deleteUser(event.id)
            is AdminUsersContract.Event.OnRevokeTrusted -> revokeTrusted(event.id)
            is AdminUsersContract.Event.OnShowCreateDialog -> setState {
                copy(
                    showCreateDialog = event.show,
                    createName = "",
                    createEmail = "",
                    createRole = "USER",
                    createPassword = ""
                )
            }
            is AdminUsersContract.Event.OnCreateFieldChanged -> {
                when (event.field) {
                    "name" -> setState { copy(createName = event.value) }
                    "email" -> setState { copy(createEmail = event.value) }
                    "role" -> setState { copy(createRole = event.value) }
                    "password" -> setState { copy(createPassword = event.value) }
                }
            }
            is AdminUsersContract.Event.OnCreateUser -> createUser()
            is AdminUsersContract.Event.OnShowRoleDialog -> {
                setState {
                    copy(
                        showRoleDialog = event.show,
                        roleTargetUser = event.user,
                        roleSelectedValue = event.user?.role ?: "USER"
                    )
                }
            }
            is AdminUsersContract.Event.OnRoleSelectedValueChange -> {
                setState { copy(roleSelectedValue = event.role) }
            }
            is AdminUsersContract.Event.OnConfirmRoleChange -> confirmRoleChange()
            is AdminUsersContract.Event.OnShowUserDetail -> setState { copy(selectedUserDetail = event.user) }
        }
    }

    private fun loadUsers() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            val roleFilter = if (uiState.value.selectedRole == "ALL") null else uiState.value.selectedRole
            val statusFilter = if (uiState.value.selectedStatus == "ALL") null else uiState.value.selectedStatus.lowercase()
            val query = if (uiState.value.searchQuery.isBlank()) null else uiState.value.searchQuery

            when (val result = getUsersUseCase(query, roleFilter, statusFilter)) {
                is NetworkResult.Success -> {
                    setState { copy(isLoading = false, users = result.data) }
                }
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false, error = result.message) }
                }
                else -> {}
            }
        }
    }

    private fun createUser() {
        val name = uiState.value.createName
        val email = uiState.value.createEmail
        val role = uiState.value.createRole
        val password = uiState.value.createPassword

        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            setEffect { AdminUsersContract.Effect.ShowSnackbar("Please fill in all required fields.") }
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            when (val result = createUserUseCase(name, email, role, password)) {
                is NetworkResult.Success -> {
                    setState { copy(isLoading = false, showCreateDialog = false) }
                    setEffect { AdminUsersContract.Effect.ShowSnackbar("User created successfully.") }
                    loadUsers()
                }
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false) }
                    setEffect { AdminUsersContract.Effect.ShowSnackbar("Failed to create user: ${result.message}") }
                }
                else -> {}
            }
        }
    }

    private fun deleteUser(id: Long) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            when (val result = deleteUserUseCase(id)) {
                is NetworkResult.Success -> {
                    setState { copy(isLoading = false) }
                    setEffect { AdminUsersContract.Effect.ShowSnackbar("User deleted successfully.") }
                    loadUsers()
                }
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false) }
                    setEffect { AdminUsersContract.Effect.ShowSnackbar("Failed to delete user: ${result.message}") }
                }
                else -> {}
            }
        }
    }

    private fun confirmRoleChange() {
        val user = uiState.value.roleTargetUser ?: return
        val role = uiState.value.roleSelectedValue

        viewModelScope.launch {
            setState { copy(isLoading = true, showRoleDialog = false) }
            when (val result = updateUserRoleUseCase(user.id, role)) {
                is NetworkResult.Success -> {
                    setState { copy(isLoading = false, roleTargetUser = null) }
                    setEffect { AdminUsersContract.Effect.ShowSnackbar("Role updated successfully.") }
                    loadUsers()
                }
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false, roleTargetUser = null) }
                    setEffect { AdminUsersContract.Effect.ShowSnackbar("Failed to update role: ${result.message}") }
                }
                else -> {}
            }
        }
    }

    private fun revokeTrusted(id: Long) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            when (val result = revokeTrustedUseCase(id)) {
                is NetworkResult.Success -> {
                    setState { copy(isLoading = false) }
                    setEffect { AdminUsersContract.Effect.ShowSnackbar("Trusted status revoked.") }
                    loadUsers()
                }
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false) }
                    setEffect { AdminUsersContract.Effect.ShowSnackbar("Failed: ${result.message}") }
                }
                else -> {}
            }
        }
    }

    private fun blockUser(id: Long) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            when (val result = blockUserUseCase(id)) {
                is NetworkResult.Success -> {
                    setState { copy(isLoading = false) }
                    setEffect { AdminUsersContract.Effect.ShowSnackbar("User blocked.") }
                    loadUsers()
                }
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false) }
                    setEffect { AdminUsersContract.Effect.ShowSnackbar("Failed: ${result.message}") }
                }
                else -> {}
            }
        }
    }

    private fun unblockUser(id: Long) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            when (val result = unblockUserUseCase(id)) {
                is NetworkResult.Success -> {
                    setState { copy(isLoading = false) }
                    setEffect { AdminUsersContract.Effect.ShowSnackbar("User unblocked.") }
                    loadUsers()
                }
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false) }
                    setEffect { AdminUsersContract.Effect.ShowSnackbar("Failed: ${result.message}") }
                }
                else -> {}
            }
        }
    }
}
