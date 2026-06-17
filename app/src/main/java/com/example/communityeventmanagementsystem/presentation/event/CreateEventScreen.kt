package com.example.communityeventmanagementsystem.presentation.event

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.communityeventmanagementsystem.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    onNavigateBack: () -> Unit
) {
    var eventName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }
    var eventTime by remember { mutableStateOf("") }
    var locationType by remember { mutableStateOf("offline") }
    var locationDetail by remember { mutableStateOf("") }

    Scaffold(
        topBar = { CreateEventTopBar(onNavigateBack) },
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
            Column {
                Text("Buat Event Baru", style = HeadlineXl, color = OnSurface)
                Text("Lengkapi detail untuk mempublikasikan acara komunitas Anda.", style = BodyMd, color = OnSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
            }

            BasicInfoSection(
                eventName = eventName,
                onEventNameChange = { eventName = it },
                selectedCategory = selectedCategory,
                onCategoryChange = { selectedCategory = it },
                capacity = capacity,
                onCapacityChange = { capacity = it },
                description = description,
                onDescriptionChange = { description = it }
            )

            TimeLocationSection(
                eventDate = eventDate,
                onDateChange = { eventDate = it },
                eventTime = eventTime,
                onTimeChange = { eventTime = it },
                locationType = locationType,
                onLocationTypeChange = { locationType = it },
                locationDetail = locationDetail,
                onLocationDetailChange = { locationDetail = it }
            )

            MediaSection()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.SpacingMd),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)
            ) {
                OutlinedButton(
                    onClick = { /* Save draft */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = Shapes.Large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Outline)
                ) {
                    Text("Simpan Draft", style = LabelMd)
                }
                Button(
                    onClick = { /* Publish */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .shadow(4.dp, Shapes.Large),
                    shape = Shapes.Large,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnPrimary)
                ) {
                    Text("Publikasikan Event", style = LabelMd)
                }
            }
            Spacer(modifier = Modifier.height(Dimens.SpacingXl))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "EventHub",
                style = HeadlineMd,
                color = Primary,
                fontWeight = FontWeight.Black
            )
        },
        navigationIcon = {
            Box(
                modifier = Modifier
                    .padding(start = Dimens.ContainerPadding, end = Dimens.SpacingSm)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(SurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = "https://lh3.googleusercontent.com/aida-public/AB6AXuA7s1mLxgcnnWrdW2MbPatBpEcUDUl5f3TrM9xAjyR2Jkgtd1UbBpcWu3_DTqbdEipzHa52EwjRs3uNdew1eaEjX5kxLgSIax-fp8fQsll3j31T6jcTMxBVsUsX6OxPEYFGLLznPHfgIZWexSE7OfOTuU3iuitJiaUQMdm4mziUDGLPGHGBrwS_i0zleiwrIRFEnbGoPY0i1lLBaVQWx1og3c0BdQUx4sw4Q-5amhoqRNIkGMA6x3_fX5_STL7g5b0pF8KUNmMa4NED",
                    contentDescription = "User profile",
                    modifier = Modifier.fillMaxSize()
                )
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Primary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface.copy(alpha = 0.8f))
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicInfoSection(
    eventName: String,
    onEventNameChange: (String) -> Unit,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    capacity: String,
    onCapacityChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit
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
                            value = if (selectedCategory.isEmpty()) "Pilih Kategori" else selectedCategory,
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
                            DropdownMenuItem(text = { Text("Technology") }, onClick = { onCategoryChange("Technology"); expanded = false })
                            DropdownMenuItem(text = { Text("Art & Design") }, onClick = { onCategoryChange("Art & Design"); expanded = false })
                            DropdownMenuItem(text = { Text("Business") }, onClick = { onCategoryChange("Business"); expanded = false })
                            DropdownMenuItem(text = { Text("Health & Wellness") }, onClick = { onCategoryChange("Health & Wellness"); expanded = false })
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
    onDateChange: (String) -> Unit,
    eventTime: String,
    onTimeChange: (String) -> Unit,
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
                    OutlinedTextField(
                        value = eventDate,
                        onValueChange = onDateChange,
                        placeholder = { Text("YYYY-MM-DD", style = BodyMd, color = Outline) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = Shapes.Large,
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = SurfaceContainerLow,
                            focusedContainerColor = SurfaceContainerLow,
                            unfocusedBorderColor = OutlineVariant,
                            focusedBorderColor = Primary
                        )
                    )
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                    Text("Jam", style = LabelMd, color = OnSurfaceVariant)
                    OutlinedTextField(
                        value = eventTime,
                        onValueChange = onTimeChange,
                        placeholder = { Text("HH:MM", style = BodyMd, color = Outline) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = Shapes.Large,
                        singleLine = true,
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

@Composable
fun MediaSection() {
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
                Icon(Icons.Default.Image, contentDescription = null, tint = Primary)
                Text("Media", style = HeadlineMd, color = Primary)
            }

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                Text("Cover Image", style = LabelMd, color = OnSurfaceVariant)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, OutlineVariant.copy(alpha = 0.5f), Shapes.ExtraLarge)
                        .background(SurfaceContainerLowest, Shapes.ExtraLarge)
                        .clickable { /* Select Image */ }
                        .padding(vertical = 48.dp, horizontal = Dimens.SpacingMd),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = OutlineVariant, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(Dimens.SpacingMd))
                        Text("Pilih Cover Image", style = LabelMd, color = Primary)
                        Text("atau drag and drop", style = BodyMd, color = OnSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("PNG, JPG, GIF up to 10MB (Rasio 2:1 direkomendasikan)", style = BodySm.copy(fontSize = 12.sp), color = Outline, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}
