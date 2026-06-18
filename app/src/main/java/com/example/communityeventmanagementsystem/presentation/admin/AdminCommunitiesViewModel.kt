package com.example.communityeventmanagementsystem.presentation.admin

import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.model.Community
import com.example.communityeventmanagementsystem.domain.usecase.admin.DeleteCommunityUseCase
import com.example.communityeventmanagementsystem.domain.usecase.admin.GetAdminCommunitiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class AdminCommunitiesContract {
    data class State(
        val isLoading: Boolean = false,
        val searchQuery: String = "",
        val communities: List<Community> = emptyList(),
        val error: String? = null
    )

    sealed class Event {
        object LoadCommunities : Event()
        data class OnSearchQueryChanged(val query: String) : Event()
        data class OnDeleteCommunity(val id: Long) : Event()
    }

    sealed class Effect {
        data class ShowSnackbar(val message: String) : Effect()
    }
}

@HiltViewModel
class AdminCommunitiesViewModel @Inject constructor(
    private val getCommunitiesUseCase: GetAdminCommunitiesUseCase,
    private val deleteCommunityUseCase: DeleteCommunityUseCase
) : BaseViewModel<AdminCommunitiesContract.State, AdminCommunitiesContract.Event, AdminCommunitiesContract.Effect>() {

    override fun createInitialState(): AdminCommunitiesContract.State = AdminCommunitiesContract.State()

    override fun handleEvent(event: AdminCommunitiesContract.Event) {
        when (event) {
            is AdminCommunitiesContract.Event.LoadCommunities -> loadCommunities()
            is AdminCommunitiesContract.Event.OnSearchQueryChanged -> {
                setState { copy(searchQuery = event.query) }
                loadCommunities()
            }
            is AdminCommunitiesContract.Event.OnDeleteCommunity -> deleteCommunity(event.id)
        }
    }

    private fun loadCommunities() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            val query = if (uiState.value.searchQuery.isBlank()) null else uiState.value.searchQuery
            when (val result = getCommunitiesUseCase(query)) {
                is NetworkResult.Success -> {
                    setState { copy(isLoading = false, communities = result.data) }
                }
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false, error = result.message) }
                }
                else -> {}
            }
        }
    }

    private fun deleteCommunity(id: Long) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            when (val result = deleteCommunityUseCase(id)) {
                is NetworkResult.Success -> {
                    setState { copy(isLoading = false) }
                    setEffect { AdminCommunitiesContract.Effect.ShowSnackbar("Community deleted successfully.") }
                    loadCommunities()
                }
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false) }
                    setEffect { AdminCommunitiesContract.Effect.ShowSnackbar("Failed to delete community: ${result.message}") }
                }
                else -> {}
            }
        }
    }
}
