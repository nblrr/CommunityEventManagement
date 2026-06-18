package com.example.communityeventmanagementsystem.presentation.profile

import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.usecase.profile.BecomeOrganizerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrganizerRegistrationViewModel @Inject constructor(
    private val becomeOrganizerUseCase: BecomeOrganizerUseCase
) : BaseViewModel<OrganizerRegistrationContract.State, OrganizerRegistrationContract.Event, OrganizerRegistrationContract.Effect>() {

    override fun createInitialState(): OrganizerRegistrationContract.State = OrganizerRegistrationContract.State()

    override fun handleEvent(event: OrganizerRegistrationContract.Event) {
        when (event) {
            is OrganizerRegistrationContract.Event.OnSubmitClicked -> becomeOrganizer()
        }
    }

    private fun becomeOrganizer() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            when (val result = becomeOrganizerUseCase()) {
                is NetworkResult.Success -> {
                    setState { copy(isLoading = false, user = result.data, isSuccess = true) }
                    setEffect { OrganizerRegistrationContract.Effect.NavigateToOrganizerDashboard }
                }
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false, error = result.message) }
                }
                is NetworkResult.Loading -> {
                    setState { copy(isLoading = true) }
                }
            }
        }
    }
}
