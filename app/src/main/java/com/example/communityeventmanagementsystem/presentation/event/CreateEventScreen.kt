package com.example.communityeventmanagementsystem.presentation.event

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.communityeventmanagementsystem.presentation.components.StandardTopAppBar
import com.example.communityeventmanagementsystem.presentation.community.CommunityMediaSection
import com.example.communityeventmanagementsystem.ui.theme.*
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    onNavigateBack: () -> Unit,
    communityIdPrefill: Long? = null,
    viewModel: CreateEventViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var eventName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var categoryId by remember { mutableLongStateOf(1L) }
    var capacity by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }
    var eventTime by remember { mutableStateOf("") }
    var locationType by remember { mutableStateOf("offline") }
    var locationDetail by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    
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

    LaunchedEffect(state.categories) {
        if (state.categories.isNotEmpty() && selectedCategory.isEmpty()) {
            selectedCategory = state.categories.first().name
            categoryId = state.categories.first().id
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
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
                    Text("Pilih")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val time = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    eventTime = time.format(DateTimeFormatter.ofPattern("HH:mm"))
                    showTimePicker = false
                }) {
                    Text("Pilih")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Batal")
                }
            },
            text = {
                TimePicker(state = timePickerState)
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
                Text("Buat Event Baru", style = HeadlineLgMobile, color = OnSurface)
                Text("Lengkapi detail untuk mempublikasikan acara komunitas Anda.", style = BodyMd, color = OnSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
            }

            BasicInfoSection(
                eventName = eventName,
                onEventNameChange = { eventName = it },
                selectedCategory = selectedCategory,
                onCategoryChange = { name, id -> 
                    selectedCategory = name
                    categoryId = id
                },
                capacity = capacity,
                onCapacityChange = { capacity = it },
                description = description,
                onDescriptionChange = { description = it },
                categories = state.categories
            )

            TimeLocationSection(
                eventDate = eventDate,
                onDateClick = { showDatePicker = true },
                eventTime = eventTime,
                onTimeClick = { showTimePicker = true },
                locationType = locationType,
                onLocationTypeChange = { locationType = it },
                locationDetail = locationDetail,
                onLocationDetailChange = { locationDetail = it }
            )

            CommunityMediaSection(
                selectedImageUri = selectedImageUri,
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
                    viewModel.handleEvent(
                        CreateEventContract.Event.CreateEvent(
                            communityId = communityIdPrefill ?: 1L, 
                            categoryId = categoryId,
                            title = eventName,
                            description = description,
                            eventDate = eventDate,
                            eventTime = eventTime,
                            location = locationDetail,
                            maxAttendees = capacity.toIntOrNull() ?: 0,
                            isOnline = locationType == "online",
                            coverImageUri = selectedImageUri
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .shadow(4.dp, Shapes.Large),
                shape = Shapes.Large,
                enabled = !state.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnPrimary)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = OnPrimary, strokeWidth = 2.dp)
                } else {
                    Text("Publikasikan Event", style = LabelMd)
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
    selectedCategory: String,
    onCategoryChange: (String, Long) -> Unit,
    capacity: String,
    onCapacityChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    categories: List<com.example.communityeventmanagementsystem.domain.model.Category>
) {
    Surface(
        shape = Shapes.ExtraLarge,
        color = Color.White.copy(alpha = 0.7f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Dimens.SpacingLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLg)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Primary)
                Text("Informasi Dasar", style = HeadlineMd, color = Primary)
            }

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                Text("Nama Kegiatan", style = LabelMd, color = OnSurfaceVariant)
                OutlinedTextField(
                    value = eventName,
                    onValueChange = onEventNameChange,
                    placeholder = { Text("Contoh: Tech Meetup Jakarta 2024", style = BodyMd, color = Outline) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = Shapes.Large,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = SurfaceContainerLow,
                        focusedContainerColor = SurfaceContainerLow,
                        unfocusedBorderColor = OutlineVariant,
                        focusedBorderColor = Primary
                    )
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingLg)) {
                var expanded by remember { mutableStateOf(false) }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                    Text("Kategori", style = LabelMd, color = OnSurfaceVariant)
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
                                unfocusedContainerColor = SurfaceContainerLow,
                                focusedContainerColor = SurfaceContainerLow,
                                unfocusedBorderColor = OutlineVariant,
                                focusedBorderColor = Primary
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = { 
                                        onCategoryChange(category.name, category.id)
                                        expanded = false 
                                    }
                                )
                            }
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                    Text("Kuota Peserta", style = LabelMd, color = OnSurfaceVariant)
                    OutlinedTextField(
                        value = capacity,
                        onValueChange = onCapacityChange,
                        placeholder = { Text("Contoh: 100", style = BodyMd, color = Outline) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = Shapes.Large,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = SurfaceContainerLow,
                            focusedContainerColor = SurfaceContainerLow,
                            unfocusedBorderColor = OutlineVariant,
                            focusedBorderColor = Primary
                        )
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                Text("Deskripsi Kegiatan", style = LabelMd, color = OnSurfaceVariant)
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    placeholder = { Text("Ceritakan tentang acara Anda secara detail...", style = BodyMd, color = Outline) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = Shapes.Large,
                    minLines = 4,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = SurfaceContainerLow,
                        focusedContainerColor = SurfaceContainerLow,
                        unfocusedBorderColor = OutlineVariant,
                        focusedBorderColor = Primary
                    )
                )
            }
        }
    }
}

@Composable
fun TimeLocationSection(
    eventDate: String,
    onDateClick: () -> Unit,
    eventTime: String,
    onTimeClick: () -> Unit,
    locationType: String,
    onLocationTypeChange: (String) -> Unit,
    locationDetail: String,
    onLocationDetailChange: (String) -> Unit
) {
    Surface(
        shape = Shapes.ExtraLarge,
        color = Color.White.copy(alpha = 0.7f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Dimens.SpacingLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLg)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                Icon(Icons.Default.Event, contentDescription = null, tint = Primary)
                Text("Waktu & Lokasi", style = HeadlineMd, color = Primary)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingLg)) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                    Text("Tanggal", style = LabelMd, color = OnSurfaceVariant)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = eventDate,
                            onValueChange = { },
                            placeholder = { Text("Pilih Tanggal", style = BodyMd, color = Outline) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = Shapes.Large,
                            readOnly = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = SurfaceContainerLow,
                                focusedContainerColor = SurfaceContainerLow,
                                unfocusedBorderColor = OutlineVariant,
                                focusedBorderColor = Primary
                            )
                        )
                        Box(modifier = Modifier.matchParentSize().clickable { onDateClick() })
                    }
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                    Text("Jam", style = LabelMd, color = OnSurfaceVariant)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = eventTime,
                            onValueChange = { },
                            placeholder = { Text("Pilih Jam", style = BodyMd, color = Outline) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = Shapes.Large,
                            readOnly = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = SurfaceContainerLow,
                                focusedContainerColor = SurfaceContainerLow,
                                unfocusedBorderColor = OutlineVariant,
                                focusedBorderColor = Primary
                            )
                        )
                        Box(modifier = Modifier.matchParentSize().clickable { onTimeClick() })
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                Text("Tipe Lokasi", style = LabelMd, color = OnSurfaceVariant)
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
                            Text("Offline", style = LabelMd, color = if (isOffline) OnPrimaryContainer else OnSurfaceVariant)
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
                            Text("Online", style = LabelMd, color = if (isOnline) OnPrimaryContainer else OnSurfaceVariant)
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                Text("Detail Lokasi / Link Meeting", style = LabelMd, color = OnSurfaceVariant)
                OutlinedTextField(
                    value = locationDetail,
                    onValueChange = onLocationDetailChange,
                    placeholder = { Text("Masukkan alamat lengkap atau link platform...", style = BodyMd, color = Outline) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = Shapes.Large,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = SurfaceContainerLow,
                        focusedContainerColor = SurfaceContainerLow,
                        unfocusedBorderColor = OutlineVariant,
                        focusedBorderColor = Primary
                    )
                )
            }
        }
    }
}
