package com.example.communityeventmanagementsystem.presentation.community

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.communityeventmanagementsystem.presentation.components.StandardTopAppBar
import com.example.communityeventmanagementsystem.presentation.components.AppCard
import com.example.communityeventmanagementsystem.ui.theme.*
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCommunityScreen(
    onNavigateBack: () -> Unit,
    communityId: Long? = null,
    viewModel: CreateCommunityViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var communityName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var categoryId by remember { mutableLongStateOf(1L) }
    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var currentImageUrl by remember { mutableStateOf<String?>(null) }

    var communityNameError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }

    fun validateCommunityName(value: String) {
        communityNameError = if (value.isBlank()) "Nama komunitas tidak boleh kosong" else null
    }

    fun validateDescription(value: String) {
        descriptionError = if (value.isBlank()) "Deskripsi tidak boleh kosong" else null
    }

    LaunchedEffect(communityId) {
        if (communityId != null && communityId != -1L) {
            viewModel.handleEvent(CreateCommunityContract.Event.LoadCommunityDetail(communityId))
        }
    }

    LaunchedEffect(state.community) {
        state.community?.let { community ->
            communityName = community.name
            description = community.description ?: ""
            categoryId = community.categoryId
            currentImageUrl = community.coverImageUrl
        }
    }

    LaunchedEffect(state.categories, state.community) {
        if (state.categories.isNotEmpty()) {
            val community = state.community
            if (community != null) {
                val category = state.categories.find { it.id == community.categoryId }
                if (category != null) {
                    selectedCategory = category.name
                    categoryId = category.id
                }
            } else if (selectedCategory.isEmpty()) {
                selectedCategory = state.categories.first().name
                categoryId = state.categories.first().id
            }
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
                is CreateCommunityContract.Effect.NavigateBack -> onNavigateBack()
                is CreateCommunityContract.Effect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        topBar = { 
            StandardTopAppBar(
                title = if (state.isEditMode) "Edit Komunitas" else "Buat Komunitas",
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
            CommunityInformationSection(
                isEditMode = state.isEditMode,
                communityName = communityName,
                onCommunityNameChange = { 
                    communityName = it 
                    validateCommunityName(it)
                },
                communityNameError = communityNameError,
                selectedCategory = selectedCategory,
                onCategoryChange = { name, id -> 
                    selectedCategory = name
                    categoryId = id
                },
                description = description,
                onDescriptionChange = { 
                    description = it 
                    validateDescription(it)
                },
                descriptionError = descriptionError,
                categories = state.categories
            )
            CommunityMediaSection(
                selectedImageUri = selectedImageUri,
                currentImageUrl = currentImageUrl,
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
                    validateCommunityName(communityName)
                    validateDescription(description)

                    if (communityNameError == null && descriptionError == null) {
                        if (state.isEditMode) {
                            viewModel.handleEvent(
                                CreateCommunityContract.Event.UpdateCommunity(
                                    id = communityId ?: 0L,
                                    name = communityName,
                                    description = description,
                                    categoryId = categoryId,
                                    coverImageUri = selectedImageUri
                                )
                            )
                        } else {
                            viewModel.handleEvent(
                                CreateCommunityContract.Event.CreateCommunity(
                                    name = communityName,
                                    description = description,
                                    categoryId = categoryId,
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
                    Text(if (state.isEditMode) "Simpan Perubahan" else "Buat Komunitas Sekarang", style = LabelLg)
                    Spacer(modifier = Modifier.width(Dimens.SpacingSm))
                    Icon(if (state.isEditMode) Icons.Default.Check else Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(Dimens.SpacingXl))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityInformationSection(
    isEditMode: Boolean,
    communityName: String,
    onCommunityNameChange: (String) -> Unit,
    communityNameError: String?,
    selectedCategory: String,
    onCategoryChange: (String, Long) -> Unit,
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
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)
        ) {
            Text(if (isEditMode) "Edit Informasi" else "Informasi Komunitas", style = TitleLg, color = Primary)

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                Text("Nama Komunitas", style = LabelLg, color = OnSurfaceVariant)
                OutlinedTextField(
                    value = communityName,
                    onValueChange = onCommunityNameChange,
                    placeholder = { Text("Contoh: Komunitas Fotografi Solo", style = BodyMd, color = OutlineVariant) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = Shapes.Large,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = SurfaceBright,
                        focusedContainerColor = SurfaceBright,
                        unfocusedBorderColor = if (communityNameError != null) Error else OutlineVariant,
                        focusedBorderColor = if (communityNameError != null) Error else Primary
                    )
                )
                if (communityNameError != null) {
                    Text(communityNameError, style = BodySm, color = Error, modifier = Modifier.padding(start = 4.dp))
                }
            }

            var expanded by remember { mutableStateOf(false) }
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
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

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                Text("Deskripsi Komunitas", style = LabelLg, color = OnSurfaceVariant)
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    placeholder = { Text("Ceritakan tentang komunitas Anda...", style = BodyMd, color = OutlineVariant) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = Shapes.Large,
                    minLines = 4,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
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
fun CommunityMediaSection(
    selectedImageUri: Uri?,
    currentImageUrl: String?,
    onSelectImage: () -> Unit
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = SurfaceContainerLowest,
        contentPadding = 20.dp
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)
        ) {
            Text("Media", style = TitleLg, color = Primary)

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                Text("Gambar Sampul", style = LabelLg, color = OnSurfaceVariant)
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, OutlineVariant.copy(alpha = 0.5f), Shapes.ExtraLarge)
                        .background(SurfaceContainerLow.copy(alpha = 0.5f), Shapes.ExtraLarge)
                        .clickable { onSelectImage() }
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(Shapes.ExtraLarge),
                            contentScale = ContentScale.Crop
                        )
                    } else if (!currentImageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = currentImageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(Shapes.ExtraLarge),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryContainer.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.CloudUpload, contentDescription = null, tint = Primary, modifier = Modifier.size(32.dp))
                            }
                            Spacer(modifier = Modifier.height(Dimens.SpacingMd))
                            Text("Klik untuk unggah gambar", style = BodyMd, color = OnSurfaceVariant, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}
