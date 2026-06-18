package com.example.communityeventmanagementsystem.presentation.event

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.communityeventmanagementsystem.presentation.components.StandardTopAppBar
import com.example.communityeventmanagementsystem.presentation.components.AppCard
import com.example.communityeventmanagementsystem.presentation.community.CommunityMediaSection
import com.example.communityeventmanagementsystem.ui.theme.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    onNavigateBack: () -> Unit,
    communityIdPrefill: Long? = null,
    viewModel: CreateEventViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var eventName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var categoryId by remember { mutableLongStateOf(1L) }
    var capacity by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }
    var eventTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var locationType by remember { mutableStateOf("offline") }
    var locationDetail by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedCommunity by remember { mutableStateOf<com.example.communityeventmanagementsystem.domain.model.Community?>(null) }
    
    var eventNameError by remember { mutableStateOf<String?>(null) }
    var capacityError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var eventDateError by remember { mutableStateOf<String?>(null) }
    var eventTimeError by remember { mutableStateOf<String?>(null) }
    var endTimeError by remember { mutableStateOf<String?>(null) }
    var locationDetailError by remember { mutableStateOf<String?>(null) }

    fun validateForm(): Boolean {
        var isValid = true
        if (eventName.isBlank()) {
            eventNameError = "Nama event tidak boleh kosong"
            isValid = false
        } else eventNameError = null

        if (capacity.isBlank() || capacity.toIntOrNull() == null || capacity.toInt() <= 0) {
            capacityError = "Kuota harus angka positif"
            isValid = false
        } else capacityError = null

        if (description.isBlank()) {
            descriptionError = "Deskripsi tidak boleh kosong"
            isValid = false
        } else descriptionError = null

        if (eventDate.isBlank()) {
            eventDateError = "Pilih tanggal event"
            isValid = false
        } else eventDateError = null

        if (eventTime.isBlank()) {
            eventTimeError = "Pilih jam event"
            isValid = false
        } else eventTimeError = null

        if (locationDetail.isBlank()) {
            locationDetailError = "Lokasi tidak boleh kosong"
            isValid = false
        } else locationDetailError = null

        if (selectedImageUri == null) {
            scope.launch {
                snackbarHostState.showSnackbar("Harap pilih gambar sampul event")
            }
            isValid = false
        }

        if (communityIdPrefill == null && selectedCommunity == null) {
            scope.launch {
                snackbarHostState.showSnackbar("Harap pilih komunitas terlebih dahulu")
            }
            isValid = false
        }

        return isValid
    }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis() - 86400000 // Allow today
            }
        }
    )
    
    val timePickerState = rememberTimePickerState(
        initialHour = 12,
        initialMinute = 0,
        is24Hour = true
    )

    val endTimePickerState = rememberTimePickerState(
        initialHour = 14,
        initialMinute = 0,
        is24Hour = true
    )

    LaunchedEffect(state.categories) {
        if (state.categories.isNotEmpty() && selectedCategory.isEmpty()) {
            selectedCategory = state.categories.first().name
            categoryId = state.categories.first().id
        }
    }

    LaunchedEffect(state.managedCommunities) {
        if (state.managedCommunities.isNotEmpty() && selectedCommunity == null) {
            selectedCommunity = state.managedCommunities.first()
        }
    }

    // Crop Image Launcher
    val cropImageLauncher = rememberLauncherForActivityResult(
        contract = CropImageContract()
    ) { result ->
        if (result.isSuccessful) {
            selectedImageUri = result.uriContent
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            cropImageLauncher.launch(
                CropImageContractOptions(
                    uri = it,
                    cropImageOptions = CropImageOptions(
                        cropShape = CropImageView.CropShape.RECTANGLE,
                        guidelines = CropImageView.Guidelines.ON
                    )
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CreateEventContract.Effect.NavigateBack -> onNavigateBack()
                is CreateEventContract.Effect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            colors = DatePickerDefaults.colors(
                containerColor = SurfaceContainerLowest
            ),
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        eventDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    }
                    showDatePicker = false
                }) {
                    Text("Pilih", color = Primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal", color = Primary)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = SurfaceContainerLowest,
                    titleContentColor = OnSurface,
                    headlineContentColor = OnSurface,
                    weekdayContentColor = OnSurfaceVariant,
                    subheadContentColor = OnSurfaceVariant,
                    navigationContentColor = OnSurface,
                    yearContentColor = OnSurface,
                    disabledYearContentColor = OnSurfaceVariant.copy(alpha = 0.38f),
                    selectedYearContentColor = OnPrimary,
                    selectedYearContainerColor = Primary,
                    dayContentColor = OnSurface,
                    disabledDayContentColor = OnSurfaceVariant.copy(alpha = 0.38f),
                    selectedDayContentColor = OnPrimary,
                    selectedDayContainerColor = Primary,
                    todayContentColor = Primary,
                    todayDateBorderColor = Primary
                )
            )
        }
    }

    if (showEndTimePicker) {
        AlertDialog(
            onDismissRequest = { showEndTimePicker = false },
            containerColor = SurfaceContainerLowest,
            confirmButton = {
                TextButton(onClick = {
                    val time = LocalTime.of(endTimePickerState.hour, endTimePickerState.minute)
                    endTime = time.format(DateTimeFormatter.ofPattern("HH:mm"))
                    showEndTimePicker = false
                }) {
                    Text("Pilih", color = Primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndTimePicker = false }) {
                    Text("Batal", color = Primary)
                }
            },
            text = {
                TimePicker(
                    state = endTimePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = SurfaceContainerLow,
                        clockDialSelectedContentColor = OnPrimary,
                        clockDialUnselectedContentColor = OnSurface,
                        selectorColor = Primary,
                        periodSelectorBorderColor = Primary,
                        periodSelectorSelectedContainerColor = PrimaryContainer,
                        periodSelectorUnselectedContainerColor = SurfaceContainerLow,
                        periodSelectorSelectedContentColor = OnPrimaryContainer,
                        periodSelectorUnselectedContentColor = OnSurfaceVariant,
                        timeSelectorSelectedContainerColor = PrimaryContainer,
                        timeSelectorUnselectedContainerColor = SurfaceContainerLow,
                        timeSelectorSelectedContentColor = OnPrimaryContainer,
                        timeSelectorUnselectedContentColor = OnSurface
                    )
                )
            }
        )
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            containerColor = SurfaceContainerLowest,
            confirmButton = {
                TextButton(onClick = {
                    val time = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    eventTime = time.format(DateTimeFormatter.ofPattern("HH:mm"))
                    showTimePicker = false
                }) {
                    Text("Pilih", color = Primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Batal", color = Primary)
                }
            },
            text = {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = SurfaceContainerLow,
                        clockDialSelectedContentColor = OnPrimary,
                        clockDialUnselectedContentColor = OnSurface,
                        selectorColor = Primary,
                        periodSelectorBorderColor = Primary,
                        periodSelectorSelectedContainerColor = PrimaryContainer,
                        periodSelectorUnselectedContainerColor = SurfaceContainerLow,
                        periodSelectorSelectedContentColor = OnPrimaryContainer,
                        periodSelectorUnselectedContentColor = OnSurfaceVariant,
                        timeSelectorSelectedContainerColor = PrimaryContainer,
                        timeSelectorUnselectedContainerColor = SurfaceContainerLow,
                        timeSelectorSelectedContentColor = OnPrimaryContainer,
                        timeSelectorUnselectedContentColor = OnSurface
                    )
                )
            }
        )
    }

    Scaffold(
        topBar = { 
            StandardTopAppBar(
                title = "Buat Event",
                onNavigateBack = onNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ContainerPadding, vertical = Dimens.SpacingLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLg)
        ) {
            Column {
                Text("Buat Event Baru", style = HeadlineSm, color = OnSurface)
                Text("Lengkapi detail untuk mempublikasikan acara komunitas Anda.", style = BodyMd, color = OnSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
            }

            if (communityIdPrefill == null) {
                CommunitySelectionSection(
                    managedCommunities = state.managedCommunities,
                    selectedCommunity = selectedCommunity,
                    onCommunityChange = { selectedCommunity = it }
                )
            }

            BasicInfoSection(
                eventName = eventName,
                onEventNameChange = { eventName = it },
                eventNameError = eventNameError,
                selectedCategory = selectedCategory,
                onCategoryChange = { name, id -> 
                    selectedCategory = name
                    categoryId = id
                },
                capacity = capacity,
                onCapacityChange = { capacity = it },
                capacityError = capacityError,
                description = description,
                onDescriptionChange = { description = it },
                descriptionError = descriptionError,
                categories = state.categories
            )

            TimeLocationSection(
                eventDate = eventDate,
                onDateClick = { showDatePicker = true },
                eventDateError = eventDateError,
                eventTime = eventTime,
                onTimeClick = { showTimePicker = true },
                eventTimeError = eventTimeError,
                endTime = endTime,
                onEndTimeClick = { showEndTimePicker = true },
                endTimeError = endTimeError,
                locationType = locationType,
                onLocationTypeChange = { locationType = it },
                locationDetail = locationDetail,
                onLocationDetailChange = { locationDetail = it },
                locationDetailError = locationDetailError
            )

            CommunityMediaSection(
                selectedImageUri = selectedImageUri,
                currentImageUrl = null,
                onSelectImage = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )

            if (state.error != null) {
                Text(state.error!!, color = Error, style = BodySm, modifier = Modifier.padding(horizontal = 4.dp))
            }

            Button(
                onClick = {
                    if (validateForm()) {
                        val finalCommunityId = communityIdPrefill ?: selectedCommunity?.id
                        if (finalCommunityId != null) {
                            viewModel.handleEvent(
                                CreateEventContract.Event.CreateEvent(
                                    communityId = finalCommunityId, 
                                    categoryId = categoryId,
                                    title = eventName,
                                    description = description,
                                    eventDate = eventDate,
                                    eventTime = eventTime,
                                    endTime = endTime.ifBlank { null },
                                    location = locationDetail,
                                    maxAttendees = capacity.toIntOrNull() ?: 0,
                                    isOnline = locationType == "online",
                                    coverImageUri = selectedImageUri
                                )
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = Shapes.ExtraLarge,
                enabled = !state.isLoading,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp,
                    disabledElevation = 0.dp,
                    hoveredElevation = 0.dp,
                    focusedElevation = 0.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = OnPrimary,
                    disabledContainerColor = Primary.copy(alpha = 0.6f),
                    disabledContentColor = OnPrimary.copy(alpha = 0.8f)
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = OnPrimary, strokeWidth = 2.dp)
                } else {
                    Text("Publikasikan Event", style = LabelLg)
                    Spacer(modifier = Modifier.width(Dimens.SpacingSm))
                    Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(Dimens.SpacingXl))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicInfoSection(
    eventName: String,
    onEventNameChange: (String) -> Unit,
    eventNameError: String?,
    selectedCategory: String,
    onCategoryChange: (String, Long) -> Unit,
    capacity: String,
    onCapacityChange: (String) -> Unit,
    capacityError: String?,
    description: String,
    onDescriptionChange: (String) -> Unit,
    descriptionError: String?,
    categories: List<com.example.communityeventmanagementsystem.domain.model.Category>
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = SurfaceContainerLowest,
        contentPadding = 20.dp
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLg)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Primary)
                Text("Informasi Dasar", style = TitleLg, color = Primary)
            }

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                Text("Nama Kegiatan", style = LabelLg, color = OnSurfaceVariant)
                OutlinedTextField(
                    value = eventName,
                    onValueChange = onEventNameChange,
                    placeholder = { Text("Contoh: Tech Meetup Jakarta 2024", style = BodyMd, color = OutlineVariant) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = Shapes.Large,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = SurfaceBright,
                        focusedContainerColor = SurfaceBright,
                        unfocusedBorderColor = if (eventNameError != null) Error else OutlineVariant,
                        focusedBorderColor = if (eventNameError != null) Error else Primary
                    )
                )
                if (eventNameError != null) {
                    Text(eventNameError, style = BodySm, color = Error, modifier = Modifier.padding(start = 4.dp))
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingLg)) {
                var expanded by remember { mutableStateOf(false) }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                    Text("Kategori", style = LabelLg, color = OnSurfaceVariant)
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory.ifEmpty { "Pilih Kategori" },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = Shapes.Large,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = SurfaceBright,
                                focusedContainerColor = SurfaceBright,
                                unfocusedBorderColor = OutlineVariant,
                                focusedBorderColor = Primary
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(SurfaceContainerLowest)
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name, style = BodyMd) },
                                    onClick = { 
                                        onCategoryChange(category.name, category.id)
                                        expanded = false 
                                    },
                                    modifier = Modifier.background(SurfaceContainerLowest)
                                )
                            }
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                    Text("Kuota Peserta", style = LabelLg, color = OnSurfaceVariant)
                    OutlinedTextField(
                        value = capacity,
                        onValueChange = onCapacityChange,
                        placeholder = { Text("Contoh: 100", style = BodyMd, color = OutlineVariant) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = Shapes.Large,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = SurfaceBright,
                            focusedContainerColor = SurfaceBright,
                            unfocusedBorderColor = if (capacityError != null) Error else OutlineVariant,
                            focusedBorderColor = if (capacityError != null) Error else Primary
                        )
                    )
                    if (capacityError != null) {
                        Text(capacityError, style = BodySm, color = Error, modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                Text("Deskripsi Kegiatan", style = LabelLg, color = OnSurfaceVariant)
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    placeholder = { Text("Ceritakan tentang acara Anda secara detail...", style = BodyMd, color = OutlineVariant) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = Shapes.Large,
                    minLines = 4,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = SurfaceBright,
                        focusedContainerColor = SurfaceBright,
                        unfocusedBorderColor = if (descriptionError != null) Error else OutlineVariant,
                        focusedBorderColor = if (descriptionError != null) Error else Primary
                    )
                )
                if (descriptionError != null) {
                    Text(descriptionError, style = BodySm, color = Error, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
    }
}

@Composable
fun TimeLocationSection(
    eventDate: String,
    onDateClick: () -> Unit,
    eventDateError: String?,
    eventTime: String,
    onTimeClick: () -> Unit,
    eventTimeError: String?,
    endTime: String,
    onEndTimeClick: () -> Unit,
    endTimeError: String?,
    locationType: String,
    onLocationTypeChange: (String) -> Unit,
    locationDetail: String,
    onLocationDetailChange: (String) -> Unit,
    locationDetailError: String?
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = SurfaceContainerLowest,
        contentPadding = 20.dp
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLg)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                Icon(Icons.Default.Event, contentDescription = null, tint = Primary)
                Text("Waktu & Lokasi", style = TitleLg, color = Primary)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingLg)) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                    Text("Tanggal", style = LabelLg, color = OnSurfaceVariant)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = eventDate,
                            onValueChange = { },
                            placeholder = { Text("Pilih Tanggal", style = BodyMd, color = OutlineVariant) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = Shapes.Large,
                            readOnly = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = SurfaceBright,
                                focusedContainerColor = SurfaceBright,
                                unfocusedBorderColor = if (eventDateError != null) Error else OutlineVariant,
                                focusedBorderColor = if (eventDateError != null) Error else Primary
                            )
                        )
                        Box(modifier = Modifier.matchParentSize().clickable { onDateClick() })
                    }
                    if (eventDateError != null) {
                        Text(eventDateError, style = BodySm, color = Error, modifier = Modifier.padding(start = 4.dp))
                    }
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                    Text("Jam Mulai", style = LabelLg, color = OnSurfaceVariant)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = eventTime,
                            onValueChange = { },
                            placeholder = { Text("Pilih Jam", style = BodyMd, color = OutlineVariant) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = Shapes.Large,
                            readOnly = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = SurfaceBright,
                                focusedContainerColor = SurfaceBright,
                                unfocusedBorderColor = if (eventTimeError != null) Error else OutlineVariant,
                                focusedBorderColor = if (eventTimeError != null) Error else Primary
                            )
                        )
                        Box(modifier = Modifier.matchParentSize().clickable { onTimeClick() })
                    }
                    if (eventTimeError != null) {
                        Text(eventTimeError, style = BodySm, color = Error, modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                Text("Jam Selesai (Opsional)", style = LabelLg, color = OnSurfaceVariant)
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { },
                        placeholder = { Text("Pilih Jam Selesai", style = BodyMd, color = OutlineVariant) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = Shapes.Large,
                        readOnly = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = SurfaceBright,
                            focusedContainerColor = SurfaceBright,
                            unfocusedBorderColor = if (endTimeError != null) Error else OutlineVariant,
                            focusedBorderColor = if (endTimeError != null) Error else Primary
                        )
                    )
                    Box(modifier = Modifier.matchParentSize().clickable { onEndTimeClick() })
                }
                if (endTimeError != null) {
                    Text(endTimeError, style = BodySm, color = Error, modifier = Modifier.padding(start = 4.dp))
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                Text("Tipe Lokasi", style = LabelLg, color = OnSurfaceVariant)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)) {
                    val isOffline = locationType == "offline"
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onLocationTypeChange("offline") },
                        shape = Shapes.Large,
                        color = if (isOffline) PrimaryContainer else Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(2.dp, if (isOffline) Primary else OutlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(Dimens.SpacingMd),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = if (isOffline) OnPrimaryContainer else OnSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Offline", style = LabelLg, color = if (isOffline) OnPrimaryContainer else OnSurfaceVariant)
                        }
                    }
                    val isOnline = locationType == "online"
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onLocationTypeChange("online") },
                        shape = Shapes.Large,
                        color = if (isOnline) PrimaryContainer else Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(2.dp, if (isOnline) Primary else OutlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(Dimens.SpacingMd),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = null, tint = if (isOnline) OnPrimaryContainer else OnSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Online", style = LabelLg, color = if (isOnline) OnPrimaryContainer else OnSurfaceVariant)
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                Text("Detail Lokasi / Link Meeting", style = LabelLg, color = OnSurfaceVariant)
                OutlinedTextField(
                    value = locationDetail,
                    onValueChange = onLocationDetailChange,
                    placeholder = { Text("Masukkan alamat lengkap atau link platform...", style = BodyMd, color = OutlineVariant) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = Shapes.Large,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = SurfaceBright,
                        focusedContainerColor = SurfaceBright,
                        unfocusedBorderColor = if (locationDetailError != null) Error else OutlineVariant,
                        focusedBorderColor = if (locationDetailError != null) Error else Primary
                    )
                )
                if (locationDetailError != null) {
                    Text(locationDetailError, style = BodySm, color = Error, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunitySelectionSection(
    managedCommunities: List<com.example.communityeventmanagementsystem.domain.model.Community>,
    selectedCommunity: com.example.communityeventmanagementsystem.domain.model.Community?,
    onCommunityChange: (com.example.communityeventmanagementsystem.domain.model.Community) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    AppCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = SurfaceContainerLowest,
        contentPadding = 20.dp
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLg)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                Icon(Icons.Default.Group, contentDescription = null, tint = Primary)
                Text("Pilih Komunitas", style = TitleLg, color = Primary)
            }

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                Text("Komunitas Penyelenggara", style = LabelLg, color = OnSurfaceVariant)
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCommunity?.name ?: "Pilih Komunitas",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = Shapes.Large,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = SurfaceBright,
                            focusedContainerColor = SurfaceBright,
                            unfocusedBorderColor = OutlineVariant,
                            focusedBorderColor = Primary
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(SurfaceContainerLowest)
                    ) {
                        managedCommunities.forEach { community ->
                            DropdownMenuItem(
                                text = { Text(community.name, style = BodyMd) },
                                onClick = { 
                                    onCommunityChange(community)
                                    expanded = false 
                                },
                                modifier = Modifier.background(SurfaceContainerLowest)
                            )
                        }
                    }
                }
                if (managedCommunities.isEmpty()) {
                    Text(
                        "Anda tidak mengelola komunitas apa pun. Harap buat komunitas terlebih dahulu.",
                        style = BodySm,
                        color = Error,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}
