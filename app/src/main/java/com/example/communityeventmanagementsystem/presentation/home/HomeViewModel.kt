package com.example.communityeventmanagementsystem.presentation.home

import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.usecase.home.GetCategoriesUseCase
import com.example.communityeventmanagementsystem.domain.usecase.home.GetMyCommunitiesUseCase
import com.example.communityeventmanagementsystem.domain.usecase.home.GetRecommendedEventsUseCase
import com.example.communityeventmanagementsystem.domain.usecase.home.GetUpcomingEventsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.example.communityeventmanagementsystem.core.session.SessionManager
import com.google.gson.Gson

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getUpcomingEventsUseCase: GetUpcomingEventsUseCase,
    private val getRecommendedEventsUseCase: GetRecommendedEventsUseCase,
    private val getMyCommunitiesUseCase: GetMyCommunitiesUseCase,
    private val sessionManager: SessionManager,
    private val gson: Gson
) : BaseViewModel<HomeContract.State, HomeContract.Event, HomeContract.Effect>() {

    init {
        viewModelScope.launch {
            sessionManager.userData.collect { json ->
                if (!json.isNullOrBlank()) {
                    try {
                        val user = gson.fromJson(json, com.example.communityeventmanagementsystem.data.remote.dto.UserDto::class.java)
                        setState { copy(userName = user.name, userAvatar = user.avatarUrl) }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun createInitialState(): HomeContract.State = HomeContract.State()

    override fun handleEvent(event: HomeContract.Event) {
        when (event) {
            is HomeContract.Event.LoadHomeData -> loadHomeData(forceRefresh = false)
            is HomeContract.Event.RefreshHomeData -> loadHomeData(forceRefresh = true)
            is HomeContract.Event.OnCategoryClicked -> setEffect { HomeContract.Effect.NavigateToCategory(event.categoryId) }
            is HomeContract.Event.OnEventClicked -> setEffect { HomeContract.Effect.NavigateToEventDetail(event.eventId) }
            is HomeContract.Event.OnCommunityClicked -> setEffect { HomeContract.Effect.NavigateToCommunityDetail(event.communityId) }
            is HomeContract.Event.OnProfileClicked -> setEffect { HomeContract.Effect.NavigateToProfile }
            is HomeContract.Event.OnNotificationClicked -> setEffect { HomeContract.Effect.NavigateToNotifications }
        }
    }

    private fun loadHomeData(forceRefresh: Boolean) {
        val hasData = uiState.value.categories.isNotEmpty()
        if (!forceRefresh && hasData && !uiState.value.isLoading) return
        
        viewModelScope.launch {
            if (!hasData || forceRefresh) {
                setState { copy(isLoading = true, error = null) }
            }
            
            val categoriesDeferred = async { getCategoriesUseCase() }
            val upcomingEventsDeferred = async { getUpcomingEventsUseCase() }
            val recommendedEventsDeferred = async { getRecommendedEventsUseCase() }
            val myCommunitiesDeferred = async { getMyCommunitiesUseCase() }

            val categoriesResult = categoriesDeferred.await()
            val upcomingEventsResult = upcomingEventsDeferred.await()
            val recommendedEventsResult = recommendedEventsDeferred.await()
            val myCommunitiesResult = myCommunitiesDeferred.await()

            val allFailed = categoriesResult is NetworkResult.Error &&
                    upcomingEventsResult is NetworkResult.Error &&
                    recommendedEventsResult is NetworkResult.Error &&
                    myCommunitiesResult is NetworkResult.Error

            setState {
                copy(
                    isLoading = false,
                    categories = if (categoriesResult is NetworkResult.Success) categoriesResult.data else categories,
                    upcomingEvents = if (upcomingEventsResult is NetworkResult.Success) upcomingEventsResult.data else upcomingEvents,
                    recommendedEvents = if (recommendedEventsResult is NetworkResult.Success) recommendedEventsResult.data else recommendedEvents,
                    myCommunities = if (myCommunitiesResult is NetworkResult.Success) myCommunitiesResult.data else myCommunities,
                    error = if (allFailed && !hasData) {
                        (categoriesResult as? NetworkResult.Error)?.message
                            ?: (upcomingEventsResult as? NetworkResult.Error)?.message
                            ?: (recommendedEventsResult as? NetworkResult.Error)?.message
                            ?: (myCommunitiesResult as? NetworkResult.Error)?.message
                    } else null
                )
            }
        }
    }
}
