package com.example.communityeventmanagement.ui.feature.community

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.domain.model.AppCategories
import com.example.communityeventmanagement.ui.components.CategoryDropdown
import com.example.communityeventmanagement.ui.theme.CommunityEventManagementTheme
import com.example.communityeventmanagement.ui.theme.ThemePreviews
import com.example.communityeventmanagement.util.ImagePickerBox
import com.example.communityeventmanagement.util.UiEvent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CreateCommunityScreen(
    onSuccess: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    viewModel: CreateCommunityViewModel = hiltViewModel(),
) {
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> onShowSnackbar(event.message)
            }
        }
    }
    CreateCommunityContent(
        name = viewModel.name,
        category = viewModel.category,
        description = viewModel.description,
        coverImageUri = viewModel.coverImageUri,
        errorMessage = viewModel.errorMessageResId?.let { stringResource(it) },
        isFormValid = viewModel.isFormValid,
        isEditMode = viewModel.isEditMode,
        onNameChange = { 
            viewModel.name = it 
            viewModel.clearErrors()
        },
        onCategoryChange = { 
            viewModel.category = it 
            viewModel.clearErrors()
        },
        onDescriptionChange = { 
            viewModel.description = it 
            viewModel.clearErrors()
        },
        onImageSelected = { viewModel.coverImageUri = it },
        onSubmitClick = { viewModel.submit(onSuccess) },
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCommunityContent(
    name: String,
    category: String,
    description: String,
    coverImageUri: String?,
    errorMessage: String?,
    isFormValid: Boolean,
    isEditMode: Boolean,
    onNameChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onImageSelected: (String?) -> Unit,
    onSubmitClick: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        stringResource(if (isEditMode) R.string.title_edit_community else R.string.title_create_community), 
                        fontWeight = FontWeight.Black
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            OutlinedTextField(
                value = name, 
                onValueChange = onNameChange, 
                label = { Text(stringResource(R.string.label_community_name)) }, 
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                singleLine = true
            )
            
            CategoryDropdown(
                label = stringResource(id = R.string.category),
                selectedCategoryId = category,
                options = AppCategories,
                onOptionSelected = onCategoryChange,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description, 
                onValueChange = onDescriptionChange, 
                label = { Text(stringResource(R.string.label_description)) }, 
                modifier = Modifier.fillMaxWidth(), 
                minLines = 3,
                maxLines = 5,
                shape = MaterialTheme.shapes.small
            )
            
            ImagePickerBox(
                imageUri = coverImageUri,
                onImageSelected = onImageSelected,
                label = stringResource(R.string.btn_choose_cover)
            )

            if (errorMessage != null) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.weight(1f))
            Button(
                onClick = onSubmitClick,
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.small
            ) { 
                Text(
                    stringResource(if (isEditMode) R.string.btn_save_changes else R.string.btn_create_now), 
                    fontWeight = FontWeight.Bold
                ) 
            }
        }
    }
}

@ThemePreviews
@Composable
fun CreateCommunityScreenPreview() {
    CommunityEventManagementTheme {
        CreateCommunityContent(
            name = "Tech Community",
            category = "TECHNOLOGY",
            description = "A group for tech enthusiasts to share knowledge.",
            coverImageUri = null,
            errorMessage = null,
            isFormValid = true,
            isEditMode = false,
            onNameChange = {},
            onCategoryChange = {},
            onDescriptionChange = {},
            onImageSelected = {},
            onSubmitClick = {},
            onNavigateBack = {}
        )
    }
}

