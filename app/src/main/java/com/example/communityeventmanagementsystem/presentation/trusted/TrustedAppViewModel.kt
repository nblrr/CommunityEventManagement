package com.example.communityeventmanagementsystem.presentation.trusted

import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.usecase.trusted.GetMyTrustedAppUseCase
import com.example.communityeventmanagementsystem.domain.usecase.trusted.SubmitTrustedAppUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrustedAppViewModel @Inject constructor(
    private val getMyTrustedAppUseCase: GetMyTrustedAppUseCase,
    private val submitTrustedAppUseCase: SubmitTrustedAppUseCase
) : BaseViewModel<TrustedAppContract.State, TrustedAppContract.Event, TrustedAppContract.Effect>() {

    override fun createInitialState(): TrustedAppContract.State = TrustedAppContract.State()

    override fun handleEvent(event: TrustedAppContract.Event) {
        when (event) {
            is TrustedAppContract.Event.LoadMyApplication -> loadMyApplication()
            is TrustedAppContract.Event.OnCommunityNameChanged -> setState { copy(communityName = event.name) }
            is TrustedAppContract.Event.OnReasonChanged -> setState { copy(reason = event.reason) }
            is TrustedAppContract.Event.OnExperienceChanged -> setState { copy(experience = event.experience) }
            is TrustedAppContract.Event.OnSubmitClicked -> submitApplication()
        }
    }

    private fun loadMyApplication() {
        if (uiState.value.application != null && !uiState.value.isLoading) return
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            when (val result = getMyTrustedAppUseCase()) {
                is NetworkResult.Success -> {
                    setState { copy(isLoading = false, application = result.data) }
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

    private fun submitApplication() {
        val name = uiState.value.communityName
        val reason = uiState.value.reason
        val experience = uiState.value.experience
        if (name.isBlank() || reason.isBlank() || experience.isBlank()) return

        viewModelScope.launch {
            setState { copy(isSubmitting = true) }
            when (val result = submitTrustedAppUseCase(name, reason, experience)) {
                is NetworkResult.Success -> {
                    setState { copy(isSubmitting = false, application = result.data) }
                    setEffect { TrustedAppContract.Effect.ShowSuccessMessage }
                }
                is NetworkResult.Error -> {
                    setState { copy(isSubmitting = false, error = result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
