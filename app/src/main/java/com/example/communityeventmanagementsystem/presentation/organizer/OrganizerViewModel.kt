package com.example.communityeventmanagementsystem.presentation.organizer

import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.usecase.organizer.DeleteEventUseCase
import com.example.communityeventmanagementsystem.domain.usecase.organizer.GetMyManagedCommunitiesUseCase
import com.example.communityeventmanagementsystem.domain.usecase.organizer.GetMyManagedEventsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrganizerViewModel @Inject constructor(
    private val getMyManagedCommunitiesUseCase: GetMyManagedCommunitiesUseCase,
    private val getMyManagedEventsUseCase: GetMyManagedEventsUseCase,
    private val deleteEventUseCase: DeleteEventUseCase
) : BaseViewModel<OrganizerContract.State, OrganizerContract.Event, OrganizerContract.Effect>() {

    override fun createInitialState(): OrganizerContract.State = OrganizerContract.State()

    override fun handleEvent(event: OrganizerContract.Event) {
        when (event) {
            is OrganizerContract.Event.LoadDashboard -> loadDashboard(forceRefresh = false)
            is OrganizerContract.Event.OnDeleteEvent -> deleteEvent(event.id)
        }
    }

    private fun loadDashboard(forceRefresh: Boolean) {
        if (!forceRefresh && uiState.value.communities.isNotEmpty() && !uiState.value.isLoading) return
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            val communitiesDeferred = async { getMyManagedCommunitiesUseCase() }
            val eventsDeferred = async { getMyManagedEventsUseCase() }

            val communitiesResult = communitiesDeferred.await()
            val eventsResult = eventsDeferred.await()

            setState {
                copy(
                    isLoading = false,
                    communities = if (communitiesResult is NetworkResult.Success) communitiesResult.data else communities,
                    events = if (eventsResult is NetworkResult.Success) eventsResult.data else events,
                    error = (communitiesResult as? NetworkResult.Error)?.message
                        ?: (eventsResult as? NetworkResult.Error)?.message
                )
            }
        }
    }

    private fun deleteEvent(id: Long) {
        viewModelScope.launch {
            when (deleteEventUseCase(id)) {
                is NetworkResult.Success -> {
                    setEffect { OrganizerContract.Effect.ShowDeleteSuccess }
                    loadDashboard(forceRefresh = true)
                }
                else -> {}
            }
        }
    }
}
