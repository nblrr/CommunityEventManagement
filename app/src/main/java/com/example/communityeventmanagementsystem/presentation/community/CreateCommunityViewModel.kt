package com.example.communityeventmanagementsystem.presentation.community

import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.model.Community
import com.example.communityeventmanagementsystem.domain.usecase.community.GetCommunityDetailUseCase
import com.example.communityeventmanagementsystem.domain.usecase.organizer.CreateCommunityUseCase
import com.example.communityeventmanagementsystem.domain.usecase.community.JoinCommunityUseCase
import com.example.communityeventmanagementsystem.domain.usecase.home.GetCategoriesUseCase
import com.example.communityeventmanagementsystem.domain.usecase.media.UploadImageUseCase
import com.example.communityeventmanagementsystem.domain.usecase.organizer.UpdateCommunityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateCommunityViewModel @Inject constructor(
    private val createCommunityUseCase: CreateCommunityUseCase,
    private val updateCommunityUseCase: UpdateCommunityUseCase,
    private val getCommunityDetailUseCase: GetCommunityDetailUseCase,
    private val joinCommunityUseCase: JoinCommunityUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val uploadImageUseCase: UploadImageUseCase
) : BaseViewModel<CreateCommunityContract.State, CreateCommunityContract.Event, CreateCommunityContract.Effect>() {

    init {
        loadCategories()
    }

    override fun createInitialState(): CreateCommunityContract.State = CreateCommunityContract.State()

    override fun handleEvent(event: CreateCommunityContract.Event) {
        when (event) {
            is CreateCommunityContract.Event.LoadCommunityDetail -> loadCommunityDetail(event.id)
            is CreateCommunityContract.Event.CreateCommunity -> createCommunity(event)
            is CreateCommunityContract.Event.UpdateCommunity -> updateCommunity(event)
        }
    }

    private fun loadCommunityDetail(id: Long) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            when (val result = getCommunityDetailUseCase(id)) {
                is NetworkResult.Success -> {
                    setState { copy(isLoading = false, community = result.data, isEditMode = true) }
                }
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false, error = result.message) }
                }
                is NetworkResult.Loading -> {}
            }
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

    private fun createCommunity(event: CreateCommunityContract.Event.CreateCommunity) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            
            var uploadedUrl: String? = null
            if (event.coverImageUri != null) {
                when (val uploadResult = uploadImageUseCase(event.coverImageUri, "community-cover")) {
                    is NetworkResult.Success -> {
                        uploadedUrl = uploadResult.data.url
                    }
                    is NetworkResult.Error -> {
                        setState { copy(isLoading = false, error = "Gagal mengunggah gambar sampul: ${uploadResult.message}") }
                        return@launch
                    }
                    is NetworkResult.Loading -> {}
                }
            }

            val community = Community(
                id = 0L,
                name = event.name,
                description = event.description,
                categoryId = event.categoryId,
                coverImageUrl = uploadedUrl,
                memberCount = 0,
                organizerId = 0L // Backend handles this, but keeping domain consistency
            )
            when (val result = createCommunityUseCase(community)) {
                is NetworkResult.Success -> {
                    // Auto-join after creation
                    val createdCommunity = result.data
                    joinCommunityUseCase(createdCommunity.id)

                    setState { copy(isLoading = false) }
                    setEffect { CreateCommunityContract.Effect.ShowMessage("Berhasil membuat komunitas!") }
                    setEffect { CreateCommunityContract.Effect.NavigateBack }
                }
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false, error = result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    private fun updateCommunity(event: CreateCommunityContract.Event.UpdateCommunity) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            
            var uploadedUrl: String? = uiState.value.community?.coverImageUrl
            if (event.coverImageUri != null) {
                when (val uploadResult = uploadImageUseCase(event.coverImageUri, "community-cover")) {
                    is NetworkResult.Success -> {
                        uploadedUrl = uploadResult.data.url
                    }
                    is NetworkResult.Error -> {
                        setState { copy(isLoading = false, error = "Gagal mengunggah gambar sampul: ${uploadResult.message}") }
                        return@launch
                    }
                    is NetworkResult.Loading -> {}
                }
            }

            val community = Community(
                id = event.id,
                name = event.name,
                description = event.description,
                categoryId = event.categoryId,
                coverImageUrl = uploadedUrl,
                memberCount = uiState.value.community?.memberCount ?: 0,
                organizerId = uiState.value.community?.organizerId ?: 0L
            )
            when (val result = updateCommunityUseCase(event.id, community)) {
                is NetworkResult.Success -> {
                    setState { copy(isLoading = false, isSuccess = true) }
                    setEffect { CreateCommunityContract.Effect.ShowMessage("Berhasil memperbarui komunitas!") }
                    setEffect { CreateCommunityContract.Effect.NavigateBack }
                }
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false, error = result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
