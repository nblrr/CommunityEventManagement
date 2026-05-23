package com.example.communityeventmanagement.ui.feature.event

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.ui.AppViewModelProvider
import com.example.communityeventmanagement.ui.components.CategoryDropdown
import com.example.communityeventmanagement.ui.components.DatePickerField
import com.example.communityeventmanagement.ui.components.SuccessBottomSheet
import com.example.communityeventmanagement.ui.components.TimePickerField
import com.example.communityeventmanagement.util.ImagePickerBox
import kotlinx.coroutines.launch

@Composable
fun CreateEventScreen(
    communityId: Int,
    eventId: Int? = null,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    viewModel: CreateEventViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(eventId, communityId) {
        if (eventId != null) {
            viewModel.loadEvent(eventId, communityId)
        }
    }

    CreateEventContent(
        title = viewModel.title,
        description = viewModel.description,
        selectedDateMillis = viewModel.selectedDateMillis,
        time = viewModel.time,
        location = viewModel.location,
        category = viewModel.category,
        maxAttendees = viewModel.maxAttendees,
        coverImageUri = viewModel.coverImageUri,
        errorMessage = viewModel.errorMessageResId?.let { stringResource(it) },
        isSubmitting = viewModel.isSubmitting,
        isFormValid = viewModel.isFormValid,
        isEditMode = viewModel.isEditMode,
        onTitleChange = { 
            viewModel.title = it 
            viewModel.clearErrors()
        },
        onDescriptionChange = { 
            viewModel.description = it 
            viewModel.clearErrors()
        },
        onDateSelected = { 
            viewModel.selectedDateMillis = it 
            viewModel.clearErrors()
        },
        onTimeSelected = { 
            viewModel.time = it 
            viewModel.clearErrors()
        },
        onLocationChange = { 
            viewModel.location = it 
            viewModel.clearErrors()
        },
        onCategoryChange = { 
            viewModel.category = it 
            viewModel.clearErrors()
        },
        onMaxAttendeesChange = { 
            viewModel.maxAttendees = it 
            viewModel.clearErrors()
        },
        onImageSelected = { viewModel.coverImageUri = it },
        onSubmit = { scope.launch { viewModel.submit(communityId) } },
        onNavigateBack = onNavigateBack
    )

    if (viewModel.showSuccessSheet) {
        SuccessBottomSheet(
            title = stringResource(if (viewModel.isEditMode) R.string.msg_event_updated else R.string.msg_event_created_title),
            subtitle = stringResource(R.string.msg_event_created_body),
            onDismiss = {
                viewModel.showSuccessSheet = false
                onNavigateBack()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventContent(
    title: String,
    description: String,
    selectedDateMillis: Long?,
    time: String,
    location: String,
    category: String,
    maxAttendees: String,
    coverImageUri: String?,
    errorMessage: String?,
    isSubmitting: Boolean,
    isFormValid: Boolean,
    isEditMode: Boolean,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDateSelected: (Long?) -> Unit,
    onTimeSelected: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onMaxAttendeesChange: (String) -> Unit,
    onImageSelected: (String?) -> Unit,
    onSubmit: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        stringResource(if (isEditMode) R.string.title_edit_event else R.string.title_create_event), 
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
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(Modifier.height(4.dp))
                
                OutlinedTextField(
                    value = title, 
                    onValueChange = onTitleChange, 
                    label = { Text(stringResource(R.string.label_event_name)) }, 
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    singleLine = true
                )

                CategoryDropdown(
                    label = stringResource(R.string.label_choose_category),
                    selectedOption = category,
                    options = com.example.communityeventmanagement.domain.entities.AppCategories,
                    onOptionSelected = onCategoryChange,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    DatePickerField(
                        label = stringResource(R.string.label_date),
                        selectedDateMillis = selectedDateMillis,
                        onDateSelected = onDateSelected,
                        modifier = Modifier.weight(1f)
                    )
                    TimePickerField(
                        label = stringResource(R.string.label_time),
                        time = time,
                        onTimeSelected = onTimeSelected,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = location, 
                    onValueChange = onLocationChange, 
                    label = { Text(stringResource(R.string.label_location)) }, 
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    singleLine = true
                )

                OutlinedTextField(
                    value = maxAttendees,
                    onValueChange = onMaxAttendeesChange,
                    label = { Text(stringResource(R.string.label_quota)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description, 
                    onValueChange = onDescriptionChange, 
                    label = { Text(stringResource(R.string.label_event_description)) }, 
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

                Spacer(Modifier.height(100.dp))
            }

            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = onSubmit,
                    enabled = isFormValid && !isSubmitting,
                    modifier = Modifier.fillMaxWidth().height(80.dp).padding(16.dp),
                    shape = MaterialTheme.shapes.small
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                    } else {
                        Text(
                            stringResource(if (isEditMode) R.string.btn_save_changes else R.string.btn_publish_event), 
                            fontWeight = FontWeight.ExtraBold, 
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
