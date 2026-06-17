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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.communityeventmanagementsystem.presentation.organizer.OrganizerContract
import com.example.communityeventmanagementsystem.presentation.organizer.OrganizerViewModel
import com.example.communityeventmanagementsystem.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCommunityStep1Screen(
    onNavigateBack: () -> Unit,
    viewModel: OrganizerViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var communityName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var categoryId by remember { mutableLongStateOf(1L) }
    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            if (effect is OrganizerContract.Effect.ShowCreateCommunitySuccess) {
                onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = { CreateCommunityTopBar(onNavigateBack) },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ContainerPadding, vertical = Dimens.SpacingLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLg)
        ) {
            CommunityInformationSection(
                communityName = communityName,
                onCommunityNameChange = { communityName = it },
                selectedCategory = selectedCategory,
                onCategoryChange = { name, id -> 
                    selectedCategory = name
                    categoryId = id
                },
                description = description,
                onDescriptionChange = { description = it }
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
                    if (communityName.isBlank() || description.isBlank()) return@Button
                    viewModel.handleEvent(
                        OrganizerContract.Event.CreateCommunity(
                            name = communityName,
                            description = description,
                            categoryId = categoryId,
                            coverImageUrl = selectedImageUri?.toString()
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
                    Text("Buat Komunitas Sekarang", style = LabelMd)
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
fun CreateCommunityTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Buat Komunitas",
                    style = HeadlineMd,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.offset(x = (-24).dp) // adjust for back button
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = OnSurfaceVariant)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface.copy(alpha = 0.8f))
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityInformationSection(
    communityName: String,
    onCommunityNameChange: (String) -> Unit,
    selectedCategory: String,
    onCategoryChange: (String, Long) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit
) {
    Surface(
        shape = Shapes.ExtraLarge,
        color = SurfaceContainerLowest,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Dimens.SpacingMd),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)
        ) {
            Text("Informasi Komunitas", style = BodyLg.copy(fontWeight = FontWeight.SemiBold), color = Primary)

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                Text("Nama Komunitas", style = LabelMd, color = OnSurfaceVariant)
                OutlinedTextField(
                    value = communityName,
                    onValueChange = onCommunityNameChange,
                    placeholder = { Text("Contoh: Komunitas Fotografi Solo", style = BodyMd, color = Outline) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = Shapes.Large,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = SurfaceBright,
                        focusedContainerColor = SurfaceBright,
                        unfocusedBorderColor = OutlineVariant,
                        focusedBorderColor = Primary
                    )
                )
            }

            var expanded by remember { mutableStateOf(false) }
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
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
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(text = { Text("Teknologi") }, onClick = { onCategoryChange("Teknologi", 1L); expanded = false })
                        DropdownMenuItem(text = { Text("Seni & Kreativitas") }, onClick = { onCategoryChange("Seni & Kreativitas", 2L); expanded = false })
                        DropdownMenuItem(text = { Text("Olahraga") }, onClick = { onCategoryChange("Olahraga", 3L); expanded = false })
                        DropdownMenuItem(text = { Text("Pendidikan") }, onClick = { onCategoryChange("Pendidikan", 4L); expanded = false })
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                Text("Deskripsi Komunitas", style = LabelMd, color = OnSurfaceVariant)
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    placeholder = { Text("Ceritakan tentang komunitas Anda...", style = BodyMd, color = Outline) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = Shapes.Large,
                    minLines = 4,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = SurfaceBright,
                        focusedContainerColor = SurfaceBright,
                        unfocusedBorderColor = OutlineVariant,
                        focusedBorderColor = Primary
                    )
                )
            }
        }
    }
}

@Composable
fun CommunityMediaSection(
    selectedImageUri: Uri?,
    onSelectImage: () -> Unit
) {
    Surface(
        shape = Shapes.ExtraLarge,
        color = SurfaceContainerLowest,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Dimens.SpacingMd),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)
        ) {
            Text("Media", style = BodyLg.copy(fontWeight = FontWeight.SemiBold), color = Primary)

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                Text("Gambar Sampul", style = LabelMd, color = OnSurfaceVariant)
                
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
