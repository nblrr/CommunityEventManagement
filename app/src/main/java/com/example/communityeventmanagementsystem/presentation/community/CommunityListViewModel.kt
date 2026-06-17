package com.example.communityeventmanagementsystem.presentation.community

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.usecase.community.GetCommunitiesUseCase
import com.example.communityeventmanagementsystem.domain.usecase.home.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityListViewModel @Inject constructor(
    private val getCommunitiesUseCase: GetCommunitiesUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<CommunityListContract.State, CommunityListContract.Event, CommunityListContract.Effect>() {

    init {
        val catId = savedStateHandle.get<Long>("categoryId")
        val categoryId = if (catId != null && catId != -1L) catId else null
        loadCategories()
        loadCommunities(categoryId, "")
    }

    override fun createInitialState(): CommunityListContract.State = CommunityListContract.State()

    override fun handleEvent(event: CommunityListContract.Event) {
        when (event) {
            is CommunityListContract.Event.LoadCommunities -> {
                val cleanCategoryId = if (event.categoryId == -1L) null else event.categoryId
                loadCommunities(cleanCategoryId, uiState.value.searchQuery)
            }
            is CommunityListContract.Event.SearchCommunities -> loadCommunities(uiState.value.categoryId, event.query)
            is CommunityListContract.Event.OnCommunityClicked -> setEffect { CommunityListContract.Effect.NavigateToCommunityDetail(event.communityId) }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            when (val result = getCategoriesUseCase()) {
                is NetworkResult.Success -> {
                    setState { copy(categories = result.data) }
                }
                else -> {}
            }
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
