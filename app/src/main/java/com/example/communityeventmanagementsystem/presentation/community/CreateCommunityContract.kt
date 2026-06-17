package com.example.communityeventmanagementsystem.presentation.community

class CreateCommunityContract {
    data class State(
        val isLoading: Boolean = false,
        val error: String? = null,
        val categories: List<com.example.communityeventmanagementsystem.domain.model.Category> = emptyList()
    )

    sealed class Event {
        data class CreateCommunity(
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
