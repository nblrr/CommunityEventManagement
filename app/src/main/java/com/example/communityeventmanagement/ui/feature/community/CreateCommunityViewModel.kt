package com.example.communityeventmanagement.ui.feature.community

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.domain.model.Community
import com.example.communityeventmanagement.domain.usecase.community.CreateCommunity
import com.example.communityeventmanagement.domain.usecase.community.GetCommunityDetail
import com.example.communityeventmanagement.domain.usecase.community.UpdateCommunity
import com.example.communityeventmanagement.domain.usecase.user.GetCurrentUser
import com.example.communityeventmanagement.domain.usecase.auth.RefreshData
import com.example.communityeventmanagement.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateCommunityViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCurrentUser: GetCurrentUser,
    private val getCommunityDetail: GetCommunityDetail,
    private val createCommunity: CreateCommunity,
    private val updateCommunity: UpdateCommunity,
    private val refreshData: RefreshData
) : ViewModel() {

    private val communityId: Int? = savedStateHandle.get<Int>("id")?.takeIf { it != -1 }

    var name by mutableStateOf("")
    var category by mutableStateOf("")
    var description by mutableStateOf("")
    var coverImageUri by mutableStateOf<String?>(null)
    var isEditMode by mutableStateOf(false)
    var existingCommunity: Community? = null
    var errorMessageResId by mutableStateOf<Int?>(null)

    init {
        communityId?.let { loadCommunity(it) }
    }

    val isFormValid: Boolean
        get() = name.isNotBlank() && category.isNotBlank() && description.isNotBlank()

    private fun loadCommunity(id: Int) {
        viewModelScope.launch {
            val community = getCommunityDetail(id).first()
            if (community != null) {
                existingCommunity = community
                isEditMode = true
                name = community.name
                category = community.category
                description = community.description
                coverImageUri = community.coverImageUri
            }
        }
    }

    fun submit(onSuccess: (Int) -> Unit) {
        val user = getCurrentUser().value ?: return
        
        viewModelScope.launch {
            if (isEditMode) {
                existingCommunity?.let { current ->
                    val updated = current.copy(
                        name = name.trim(),
                        category = category.trim(),
                        description = description.trim(),
                        coverImageUri = coverImageUri
                    )
                    when (updateCommunity(updated)) {
                        is Resource.Success -> {
                            refreshData(user)
                            onSuccess(current.id)
                        }
                        is Resource.Error -> {
                            errorMessageResId = com.example.communityeventmanagement.R.string.msg_no_data_found
                        }
                        is Resource.Loading -> {}
                    }
                }
            } else {
                // For simplified new ID generation logic (normally handled by repo/db)
                val newId = (System.currentTimeMillis() % 100000).toInt() 
                val newCommunity = Community(
                    id = newId,
                    name = name.trim(),
                    category = category.trim(),
                    description = description.trim(),
                    coverImageUri = coverImageUri,
                    organizerId = user.id,
                    organizerName = user.name,
                    memberCount = 0
                )
                when (createCommunity(newCommunity)) {
                    is Resource.Success -> {
                        refreshData(user)
                        onSuccess(newId)
                    }
                    is Resource.Error -> {
                        errorMessageResId = com.example.communityeventmanagement.R.string.msg_no_data_found
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun clearErrors() {
        errorMessageResId = null
    }
}

