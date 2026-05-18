package com.example.communityeventmanagement.features.event

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.ui.AppViewModelProvider
import com.example.communityeventmanagement.ui.theme.CommunityEventManagementTheme
import com.example.communityeventmanagement.ui.theme.ThemePreviews
import com.example.communityeventmanagement.util.ImagePickerBox
import com.example.communityeventmanagement.util.TimePickerDialog
import com.example.communityeventmanagement.util.toDateString
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@Composable
fun CreateEventScreen(
    communityId: Int,
    onCreateSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CreateEventViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scope = rememberCoroutineScope()
    
    CreateEventContent(
        title = viewModel.title,
        category = viewModel.category,
        selectedDateMillis = viewModel.selectedDateMillis,
        time = viewModel.time,
        location = viewModel.location,
        description = viewModel.description,
        coverImageUri = viewModel.coverImageUri,
        categoryOptions = viewModel.categoryOptions,
        isDateTimeValid = viewModel.isDateTimeValid,
        isTimeValid = viewModel.isTimeValid,
        isSubmitting = viewModel.isSubmitting,
        isFormValid = viewModel.isFormValid,
        showSuccessSheet = viewModel.showSuccessSheet,
        onTitleChange = { viewModel.title = it },
        onCategoryChange = { viewModel.category = it },
        onDateChange = { viewModel.selectedDateMillis = it },
        onTimeChange = { viewModel.time = it },
        onLocationChange = { viewModel.location = it },
        onDescriptionChange = { viewModel.description = it },
        onImageSelected = { viewModel.coverImageUri = it },
        onSubmit = { scope.launch { viewModel.submit(communityId) } },
        onCreateSuccess = onCreateSuccess,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventContent(
    title: String,
    category: String,
    selectedDateMillis: Long?,
    time: String,
    location: String,
    description: String,
    coverImageUri: String?,
    categoryOptions: List<String>,
    isDateTimeValid: Boolean,
    isTimeValid: Boolean,
    isSubmitting: Boolean,
    isFormValid: Boolean,
    showSuccessSheet: Boolean,
    onTitleChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onDateChange: (Long?) -> Unit,
    onTimeChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onImageSelected: (String?) -> Unit,
    onSubmit: () -> Unit,
    onCreateSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                return utcTimeMillis >= calendar.timeInMillis
            }
        }
    )

    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = if (time.isNotBlank()) time.split(".")[0].toIntOrNull() ?: 12 else 12,
        initialMinute = if (time.isNotBlank() && time.contains(".")) time.split(".")[1].toIntOrNull() ?: 0 else 0
    )

    if (showSuccessSheet) {
        ModalBottomSheet(onDismissRequest = onCreateSuccess) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(72.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(16.dp))
                Text(stringResource(R.string.msg_event_created_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Text(stringResource(R.string.msg_event_created_body), textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
                Spacer(Modifier.height(32.dp))
                Button(onClick = onCreateSuccess, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.status_finished)) }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_create_event), fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ImagePickerBox(
                imageUri = coverImageUri,
                onImageSelected = onImageSelected,
                label = stringResource(R.string.btn_choose_cover)
            )

            CreateInput(label = stringResource(R.string.label_event_name), value = title, onValueChange = onTitleChange, icon = Icons.AutoMirrored.Filled.EventNote)
            
            Text(stringResource(R.string.label_choose_category), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categoryOptions.take(3).forEach { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = { onCategoryChange(cat) },
                        label = { Text(cat) }
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f).clickable { showDatePicker = true }) {
                    CreateInput(label = stringResource(R.string.label_date), value = selectedDateMillis?.toDateString() ?: "", onValueChange = {}, icon = Icons.Default.CalendarToday, enabled = false)
                }
                Box(modifier = Modifier.weight(1f).clickable { showTimePicker = true }) {
                    CreateInput(
                        label = stringResource(R.string.label_time),
                        value = time,
                        onValueChange = {},
                        icon = Icons.Default.Schedule,
                        enabled = false,
                        isError = !isTimeValid && time.isNotBlank(),
                        supportingText = if (!isTimeValid && time.isNotBlank()) "Format jam tidak valid (HH.mm)" else null
                    )
                }
            }

            if (!isDateTimeValid && selectedDateMillis != null) {
                Text(
                    text = "Tanggal & waktu harus lebih dari saat ini",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            CreateInput(label = stringResource(R.string.label_location), value = location, onValueChange = onLocationChange, icon = Icons.Default.LocationOn)
            
            CreateInput(label = stringResource(R.string.label_event_description), value = description, onValueChange = onDescriptionChange, icon = Icons.Default.Description, singleLine = false)

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onSubmit,
                enabled = isFormValid && !isSubmitting,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isSubmitting) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text(stringResource(R.string.btn_publish_event), fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { 
                    onDateChange(datePickerState.selectedDateMillis)
                    showDatePicker = false 
                }) { Text(stringResource(R.string.btn_choose)) }
            }
        ) { DatePicker(state = datePickerState) }
    }

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val formattedTime = String.format(Locale.ROOT, "%02d.%02d", timePickerState.hour, timePickerState.minute)
                    onTimeChange(formattedTime)
                    showTimePicker = false
                }) { Text(stringResource(R.string.btn_choose)) }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

@Composable
private fun CreateInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        enabled = enabled,
        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = singleLine,
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } }
    )
}

@ThemePreviews
@Composable
fun CreateEventScreenPreview() {
    CommunityEventManagementTheme {
        CreateEventContent(
            title = "Workshop Jetpack Compose",
            category = "Pendidikan",
            selectedDateMillis = null,
            time = "10.00",
            location = "Gedung Serbaguna",
            description = "Belajar membuat UI dengan Compose.",
            coverImageUri = null,
            categoryOptions = listOf("Pendidikan", "Sosial", "Hobi"),
            isDateTimeValid = true,
            isTimeValid = true,
            isSubmitting = false,
            isFormValid = true,
            showSuccessSheet = false,
            onTitleChange = {},
            onCategoryChange = {},
            onDateChange = {},
            onTimeChange = {},
            onLocationChange = {},
            onDescriptionChange = {},
            onImageSelected = {},
            onSubmit = {},
            onCreateSuccess = {},
            onNavigateBack = {}
        )
    }
}
