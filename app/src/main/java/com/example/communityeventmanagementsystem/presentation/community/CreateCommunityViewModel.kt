package com.example.communityeventmanagementsystem.presentation.community

import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.model.Community
import com.example.communityeventmanagementsystem.domain.usecase.organizer.CreateCommunityUseCase
import com.example.communityeventmanagementsystem.domain.usecase.community.JoinCommunityUseCase
import com.example.communityeventmanagementsystem.domain.usecase.home.GetCategoriesUseCase
import com.example.communityeventmanagementsystem.domain.usecase.media.UploadImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateCommunityViewModel @Inject constructor(
    private val createCommunityUseCase: CreateCommunityUseCase,
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
            is CreateCommunityContract.Event.CreateCommunity -> createCommunity(event)
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
                memberCount = 0
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
}
