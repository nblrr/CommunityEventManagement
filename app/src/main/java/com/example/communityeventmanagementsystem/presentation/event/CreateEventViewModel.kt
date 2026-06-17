package com.example.communityeventmanagementsystem.presentation.event

import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.ui.BaseViewModel
import com.example.communityeventmanagementsystem.domain.model.Event
import com.example.communityeventmanagementsystem.domain.usecase.organizer.CreateEventUseCase
import com.example.communityeventmanagementsystem.domain.usecase.home.GetCategoriesUseCase
import com.example.communityeventmanagementsystem.domain.usecase.media.UploadImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val createEventUseCase: CreateEventUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val uploadImageUseCase: UploadImageUseCase
) : BaseViewModel<CreateEventContract.State, CreateEventContract.Event, CreateEventContract.Effect>() {

    init {
        loadCategories()
    }

    override fun createInitialState(): CreateEventContract.State = CreateEventContract.State()

    override fun handleEvent(event: CreateEventContract.Event) {
        when (event) {
            is CreateEventContract.Event.CreateEvent -> createEvent(event)
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

    private fun createEvent(event: CreateEventContract.Event.CreateEvent) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            
            var uploadedUrl: String? = null
            if (event.coverImageUri != null) {
                when (val uploadResult = uploadImageUseCase(event.coverImageUri, "event-banner")) {
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

            val eventDomain = Event(
                id = 0L,
                title = event.title,
                description = event.description,
                eventDate = event.eventDate,
                eventTime = event.eventTime,
                location = event.location,
                maxAttendees = event.maxAttendees,
                isOnline = event.isOnline,
                status = "UPCOMING",
                coverImageUrl = uploadedUrl,
                communityId = event.communityId,
                categoryId = event.categoryId,
                attendeeCount = 0
            )
            when (val result = createEventUseCase(eventDomain)) {
                is NetworkResult.Success -> {
                    setState { copy(isLoading = false) }
                    setEffect { CreateEventContract.Effect.ShowMessage("Berhasil membuat event!") }
                    setEffect { CreateEventContract.Effect.NavigateBack }
                }
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false, error = result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
