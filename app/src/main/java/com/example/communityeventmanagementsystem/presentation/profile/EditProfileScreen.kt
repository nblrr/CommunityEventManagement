package com.example.communityeventmanagementsystem.presentation.profile

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.communityeventmanagementsystem.presentation.components.ProfileAvatar
import com.example.communityeventmanagementsystem.presentation.components.AppButton
import com.example.communityeventmanagementsystem.presentation.components.AppError
import com.example.communityeventmanagementsystem.presentation.components.AppTextField
import com.example.communityeventmanagementsystem.ui.theme.*
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.example.communityeventmanagementsystem.core.common.DateTimeUtils
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            if (effect is ProfileContract.Effect.ProfileUpdated) {
                onNavigateBack()
            }
        }
    }

    var name by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    
    var nameError by remember { mutableStateOf<String?>(null) }
    var hasInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(state.user) {
        state.user?.let { user ->
            if (!hasInitialized) {
                name = user.name
                bio = user.bio ?: ""
                phoneNumber = user.phoneNumber ?: ""
                gender = user.gender ?: ""
                birthDate = user.birthDate ?: ""
                hasInitialized = true
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
                        birthDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
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

    // Crop Image Launcher
    val cropImageLauncher = rememberLauncherForActivityResult(
        contract = CropImageContract()
    ) { result ->
        if (result.isSuccessful) {
            result.uriContent?.let { croppedUri ->
                val file = getFileFromUri(context, croppedUri)
                if (file != null) {
                    viewModel.setEvent(ProfileContract.Event.UploadAvatar(file))
                }
            }
        }
    }

    // Media Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            cropImageLauncher.launch(
                CropImageContractOptions(
                    uri = it,
                    cropImageOptions = CropImageOptions(
                        cropShape = CropImageView.CropShape.OVAL,
                        guidelines = CropImageView.Guidelines.ON,
                        aspectRatioX = 1,
                        aspectRatioY = 1,
                        fixAspectRatio = true
                    )
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.setEvent(ProfileContract.Event.LoadProfile)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .imePadding()
        ) {
            if (state.isLoading && state.user == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.error != null && state.user == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AppError(
                        message = state.error ?: "Gagal memuat data profil",
                        onRetry = { viewModel.setEvent(ProfileContract.Event.LoadProfile) }
                    )
                }
            } else {
                state.user?.let { user ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // Avatar Selection Frame
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                        ) {
                            ProfileAvatar(
                                imageUrl = user.avatarUrl,
                                name = user.name,
                                modifier = Modifier
                                    .size(120.dp)
                                    .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
                                textStyle = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold)
                            )
                            
                            // Edit Badge
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .offset(x = (-4).dp, y = (-4).dp)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .border(3.dp, MaterialTheme.colorScheme.background, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Ubah Foto",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Forms
                        Column(
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AppTextField(
                                value = name,
                                onValueChange = {
                                    name = it
                                    nameError = if (it.isBlank()) "Nama tidak boleh kosong" else null
                                },
                                label = "Nama Lengkap",
                                error = nameError,
                                modifier = Modifier.fillMaxWidth()
                            )

                            AppTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                label = "Nomor Telepon",
                                modifier = Modifier.fillMaxWidth()
                            )

                            AppTextField(
                                value = bio,
                                onValueChange = { bio = it },
                                label = "Bio / Deskripsi Singkat",
                                singleLine = false,
                                minLines = 3,
                                modifier = Modifier.fillMaxWidth()
                            )

                            AppTextField(
                                value = gender,
                                onValueChange = { gender = it },
                                label = "Jenis Kelamin",
                                modifier = Modifier.fillMaxWidth()
                            )

                            Box(modifier = Modifier.fillMaxWidth()) {
                                AppTextField(
                                    value = DateTimeUtils.formatBirthDate(birthDate),
                                    onValueChange = { },
                                    label = "Tanggal Lahir",
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = "Pilih tanggal lahir"
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable { showDatePicker = true }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Submit Button
                        AppButton(
                            text = "Simpan Perubahan",
                            onClick = {
                                if (name.isBlank()) {
                                    nameError = "Nama tidak boleh kosong"
                                    return@AppButton
                                }
                                val updatedUser = user.copy(
                                    name = name,
                                    bio = bio,
                                    phoneNumber = phoneNumber,
                                    gender = gender,
                                    birthDate = birthDate
                                )
                                viewModel.setEvent(ProfileContract.Event.UpdateProfile(updatedUser))
                            },
                            isLoading = state.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

private fun getFileFromUri(context: Context, uri: Uri): File? {
    return try {
        val contentResolver = context.contentResolver
        val tempFile = File.createTempFile("avatar_", ".jpg", context.cacheDir)
        contentResolver.openInputStream(uri)?.use { inputStream ->
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        tempFile
    } catch (e: Exception) {
        null
    }
}
