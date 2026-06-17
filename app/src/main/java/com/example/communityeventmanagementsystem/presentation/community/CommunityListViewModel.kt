package com.example.communityeventmanagementsystem.presentation.community

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.usecase.community.GetCommunitiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CommunityListViewModel @Inject constructor(
    private val getCommunitiesUseCase: GetCommunitiesUseCase
) : BaseViewModel<CommunityListContract.State, CommunityListContract.Event, CommunityListContract.Effect>() {

    override fun createInitialState(): CommunityListContract.State = CommunityListContract.State()

    override fun handleEvent(event: CommunityListContract.Event) {
        when (event) {
            is CommunityListContract.Event.LoadCommunities -> loadCommunities(event.categoryId, uiState.value.searchQuery)
            is CommunityListContract.Event.SearchCommunities -> loadCommunities(uiState.value.categoryId, event.query)
            is CommunityListContract.Event.OnCommunityClicked -> setEffect { CommunityListContract.Effect.NavigateToCommunityDetail(event.communityId) }
        }
    }

    private fun loadCommunities(categoryId: Long?, query: String) {
        if (uiState.value.isInitialized && uiState.value.categoryId == categoryId && uiState.value.searchQuery == query) {
            return
        }
        val communitiesFlow = getCommunitiesUseCase(categoryId, query)
            .cachedIn(viewModelScope)
        
        setState { copy(categoryId = categoryId, searchQuery = query, communities = communitiesFlow, isInitialized = true) }
    }
}
