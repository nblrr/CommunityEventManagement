package com.example.communityeventmanagementsystem.presentation.community

class CreateCommunityContract {
    data class State(
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val error: String? = null,
        val categories: List<com.example.communityeventmanagementsystem.domain.model.Category> = emptyList(),
        val community: com.example.communityeventmanagementsystem.domain.model.Community? = null,
        val isEditMode: Boolean = false
    )

    sealed class Event {
        data class LoadCommunityDetail(val id: Long) : Event()
        data class CreateCommunity(
            val name: String,
            val description: String,
            val categoryId: Long,
            val coverImageUri: android.net.Uri?
        ) : Event()
        data class UpdateCommunity(
            val id: Long,
            val name: String,
            val description: String,
            val categoryId: Long,
            val coverImageUri: android.net.Uri?
        ) : Event()
    }

    sealed class Effect {
        object NavigateBack : Effect()
        data class ShowMessage(val message: String) : Effect()
    }
}
