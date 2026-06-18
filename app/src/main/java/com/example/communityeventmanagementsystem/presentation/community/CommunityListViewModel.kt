package com.example.communityeventmanagementsystem.presentation.community

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.usecase.community.GetCommunitiesUseCase
import com.example.communityeventmanagementsystem.domain.usecase.home.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityListViewModel @Inject constructor(
    private val getCommunitiesUseCase: GetCommunitiesUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel<CommunityListContract.State, CommunityListContract.Event, CommunityListContract.Effect>() {

    private var searchJob: kotlinx.coroutines.Job? = null

    init {
        val catId = savedStateHandle.get<Long>("categoryId")
        val categoryId = if (catId != null && catId != -1L) catId else null
        val query = savedStateHandle.get<String>("searchQuery") ?: ""
        val sortBy = savedStateHandle.get<String>("sortBy")

        setState {
            copy(
                categoryId = categoryId,
                searchQuery = query,
                sortBy = sortBy
            )
        }

        loadCategories()
        loadCommunities(categoryId, query, sortBy)
    }

    override fun createInitialState(): CommunityListContract.State = CommunityListContract.State()

    override fun handleEvent(event: CommunityListContract.Event) {
        when (event) {
            is CommunityListContract.Event.LoadCommunities -> {
                val cleanCategoryId = if (event.categoryId == -1L) null else event.categoryId
                loadCommunities(cleanCategoryId, uiState.value.searchQuery, event.sortBy)
            }
            is CommunityListContract.Event.SearchCommunities -> {
                searchJob?.cancel()
                setState { copy(searchQuery = event.query) }
                savedStateHandle["searchQuery"] = event.query
                if (event.immediate) {
                    loadCommunities(uiState.value.categoryId, event.query, uiState.value.sortBy)
                } else {
                    searchJob = viewModelScope.launch {
                        delay(600)
                        loadCommunities(uiState.value.categoryId, event.query, uiState.value.sortBy)
                    }
                }
            }
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

    private fun loadCommunities(categoryId: Long?, query: String, sortBy: String?) {
        if (uiState.value.isInitialized && 
            uiState.value.categoryId == categoryId && 
            uiState.value.searchQuery == query &&
            uiState.value.sortBy == sortBy) {
            return
        }

        savedStateHandle["categoryId"] = categoryId ?: -1L
        savedStateHandle["searchQuery"] = query
        savedStateHandle["sortBy"] = sortBy

        val communitiesFlow = getCommunitiesUseCase(categoryId, query, sortBy)
            .cachedIn(viewModelScope)
        
        setState { 
            copy(
                categoryId = categoryId, 
                searchQuery = query, 
                sortBy = sortBy,
                communities = communitiesFlow, 
                isInitialized = true
            ) 
        }
    }
}
