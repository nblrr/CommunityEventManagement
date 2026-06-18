package com.example.communityeventmanagementsystem.presentation.community

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.usecase.community.GetCommunityDetailUseCase
import com.example.communityeventmanagementsystem.domain.usecase.community.JoinCommunityUseCase
import com.example.communityeventmanagementsystem.domain.usecase.community.LeaveCommunityUseCase
import com.example.communityeventmanagementsystem.domain.usecase.home.GetMyCommunitiesUseCase
import com.example.communityeventmanagementsystem.domain.usecase.organizer.DeleteCommunityUseCase
import com.example.communityeventmanagementsystem.core.session.SessionManager
import com.google.gson.Gson
import com.example.communityeventmanagementsystem.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityDetailViewModel @Inject constructor(
    private val getCommunityDetailUseCase: GetCommunityDetailUseCase,
    private val joinCommunityUseCase: JoinCommunityUseCase,
    private val leaveCommunityUseCase: LeaveCommunityUseCase,
    private val getMyCommunitiesUseCase: GetMyCommunitiesUseCase,
    private val deleteCommunityUseCase: DeleteCommunityUseCase,
    private val sessionManager: SessionManager,
    private val gson: Gson,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<CommunityDetailContract.State, CommunityDetailContract.Event, CommunityDetailContract.Effect>() {

    init {
        savedStateHandle.get<Long>("id")?.let { id ->
            loadDetail(id, forceRefresh = false)
        }
    }

    override fun createInitialState(): CommunityDetailContract.State = CommunityDetailContract.State()

    override fun handleEvent(event: CommunityDetailContract.Event) {
        when (event) {
            is CommunityDetailContract.Event.LoadDetail -> loadDetail(event.id, forceRefresh = false)
            is CommunityDetailContract.Event.JoinCommunity -> join()
            is CommunityDetailContract.Event.LeaveCommunity -> leave()
            is CommunityDetailContract.Event.DeleteCommunity -> delete()
            is CommunityDetailContract.Event.ShowErrorMessage -> setState { copy(error = event.message) }
        }
    }

    private fun loadDetail(id: Long, forceRefresh: Boolean) {
        if (!forceRefresh && uiState.value.community?.id == id && !uiState.value.isLoading) return
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            val detailResult = getCommunityDetailUseCase(id)
            val myCommResult = getMyCommunitiesUseCase()
            val userDataJson = sessionManager.userData.firstOrNull()
            val currentUser = userDataJson?.let { gson.fromJson(it, User::class.java) }

            if (detailResult is NetworkResult.Success) {
                val community = detailResult.data
                val isJoined = if (myCommResult is NetworkResult.Success) {
                    myCommResult.data.any { it.id == id }
                } else {
                    false
                }
                val isCreator = currentUser?.id == community.organizerId
                setState { 
                    copy(
                        isLoading = false, 
                        community = community, 
                        isJoined = isJoined, 
                        isCreator = isCreator,
                        error = null
                    ) 
                }
            } else if (detailResult is NetworkResult.Error) {
                setState { copy(isLoading = false, error = detailResult.message) }
            }
        }
    }

    private fun join() {
        val communityId = uiState.value.community?.id ?: return
        viewModelScope.launch {
            setState { copy(isJoining = true) }
            when (val result = joinCommunityUseCase(communityId)) {
                is NetworkResult.Success -> {
                    setState { copy(isJoining = false, isJoined = true) }
                    setEffect { CommunityDetailContract.Effect.ShowMessage("Berhasil bergabung dengan komunitas!") }
                    loadDetail(communityId, forceRefresh = true)
                }
                is NetworkResult.Error -> {
                    setState { copy(isJoining = false) }
                    setEffect { CommunityDetailContract.Effect.ShowMessage(result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    private fun leave() {
        val communityId = uiState.value.community?.id ?: return
        viewModelScope.launch {
            setState { copy(isJoining = true) }
            when (val result = leaveCommunityUseCase(communityId)) {
                is NetworkResult.Success -> {
                    setState { copy(isJoining = false, isJoined = false) }
                    setEffect { CommunityDetailContract.Effect.ShowMessage("Berhasil keluar dari komunitas.") }
                    loadDetail(communityId, forceRefresh = true)
                }
                is NetworkResult.Error -> {
                    setState { copy(isJoining = false) }
                    setEffect { CommunityDetailContract.Effect.ShowMessage(result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    private fun delete() {
        val communityId = uiState.value.community?.id ?: return
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            when (val result = deleteCommunityUseCase(communityId)) {
                is NetworkResult.Success -> {
                    setState { copy(isLoading = false) }
                    setEffect { CommunityDetailContract.Effect.ShowMessage("Komunitas berhasil dihapus.") }
                    setEffect { CommunityDetailContract.Effect.NavigateBack }
                }
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false) }
                    setEffect { CommunityDetailContract.Effect.ShowMessage(result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
