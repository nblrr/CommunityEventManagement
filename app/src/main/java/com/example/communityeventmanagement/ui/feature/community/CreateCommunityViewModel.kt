package com.example.communityeventmanagement.ui.feature.community

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.domain.entities.Community
import com.example.communityeventmanagement.domain.usecase.CreateCommunity
import com.example.communityeventmanagement.domain.usecase.GetCommunityDetail
import com.example.communityeventmanagement.domain.usecase.GetCurrentUser
import com.example.communityeventmanagement.domain.usecase.RefreshData
import com.example.communityeventmanagement.domain.usecase.UpdateCommunity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CreateCommunityViewModel(
    private val getCurrentUser: GetCurrentUser,
    private val getCommunityDetail: GetCommunityDetail,
    private val createCommunity: CreateCommunity,
    private val updateCommunity: UpdateCommunity,
    private val refreshData: RefreshData
) : ViewModel() {
    var name by mutableStateOf("")
    var category by mutableStateOf("")
    var description by mutableStateOf("")
    var coverImageUri by mutableStateOf<String?>(null)
    var isEditMode by mutableStateOf(false)
    var existingCommunity: Community? = null
    var errorMessageResId by mutableStateOf<Int?>(null)

    val isFormValid: Boolean
        get() = name.isNotBlank() && category.isNotBlank() && description.isNotBlank()

    fun loadCommunity(id: Int) {
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
                    val result = updateCommunity(updated)
                    if (result.isSuccess) {
                        refreshData(user)
                        onSuccess(current.id)
                    } else {
                        errorMessageResId = com.example.communityeventmanagement.R.string.msg_no_data_found
                    }
                }
            } else {
                // For simplified new ID generation logic (normally handled by repo/db)
                // We'd ideally get all communities then max id, but for now:
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
                val result = createCommunity(newCommunity)
                if (result.isSuccess) {
                    refreshData(user)
                    onSuccess(newId)
                } else {
                    errorMessageResId = com.example.communityeventmanagement.R.string.msg_no_data_found
                }
            }
        }
    }

    fun clearErrors() {
        errorMessageResId = null
    }
}
