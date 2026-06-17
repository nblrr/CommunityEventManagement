package com.example.communityeventmanagementsystem.presentation.community

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.communityeventmanagementsystem.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCommunityStep2Screen(
    onNavigateForward: () -> Unit
) {
    var communityName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Scaffold(
        topBar = { CreateCommunityStep2TopBar() },
        containerColor = Background,
        bottomBar = { CreateCommunityStep2BottomBar(onNavigateForward) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ContainerPadding, vertical = Dimens.SpacingLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLg)
        ) {
            Text(
                text = "Lengkapi data profil komunitas untuk memudahkan anggota baru menemukan dan bergabung.",
                style = BodySm,
                color = OnSurfaceVariant
            )

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingXs)) {
                Text("Nama Komunitas", style = LabelMd, color = OnSurface, modifier = Modifier.padding(start = 4.dp))
                OutlinedTextField(
                    value = communityName,
                    onValueChange = { communityName = it },
                    placeholder = { Text("Contoh: Design Enthusiast Jakarta", style = BodyMd, color = Outline) },
                    modifier = Modifier.fillMaxWidth().shadow(1.dp, Shapes.Large),
                    shape = Shapes.Large,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = SurfaceContainerLowest,
                        focusedContainerColor = SurfaceContainerLowest,
                        unfocusedBorderColor = OutlineVariant,
                        focusedBorderColor = Primary
                    )
                )
            }

            var expanded by remember { mutableStateOf(false) }
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingXs)) {
                Text("Kategori", style = LabelMd, color = OnSurface, modifier = Modifier.padding(start = 4.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = if (selectedCategory.isEmpty()) "Pilih Kategori" else selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().shadow(1.dp, Shapes.Large).menuAnchor(),
                        shape = Shapes.Large,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = SurfaceContainerLowest,
                            focusedContainerColor = SurfaceContainerLowest,
                            unfocusedBorderColor = OutlineVariant,
                            focusedBorderColor = Primary
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(text = { Text("Teknologi & Inovasi") }, onClick = { selectedCategory = "Teknologi & Inovasi"; expanded = false })
                        DropdownMenuItem(text = { Text("Seni & Kreativitas") }, onClick = { selectedCategory = "Seni & Kreativitas"; expanded = false })
                        DropdownMenuItem(text = { Text("Olahraga & Kebugaran") }, onClick = { selectedCategory = "Olahraga & Kebugaran"; expanded = false })
                        DropdownMenuItem(text = { Text("Bisnis & Karir") }, onClick = { selectedCategory = "Bisnis & Karir"; expanded = false })
                        DropdownMenuItem(text = { Text("Sosial & Relawan") }, onClick = { selectedCategory = "Sosial & Relawan"; expanded = false })
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingXs)) {
                Text("Deskripsi", style = LabelMd, color = OnSurface, modifier = Modifier.padding(start = 4.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Ceritakan tujuan dan kegiatan utama komunitas ini...", style = BodyMd, color = Outline) },
                    modifier = Modifier.fillMaxWidth().shadow(1.dp, Shapes.Large),
                    shape = Shapes.Large,
                    minLines = 4,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = SurfaceContainerLowest,
                        focusedContainerColor = SurfaceContainerLowest,
                        unfocusedBorderColor = OutlineVariant,
                        focusedBorderColor = Primary
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingXs)) {
                Text("Cover Image", style = LabelMd, color = OnSurface, modifier = Modifier.padding(start = 4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(192.dp)
                        .border(2.dp, OutlineVariant.copy(alpha = 0.5f), Shapes.ExtraLarge)
                        .background(SurfaceContainerLow, Shapes.ExtraLarge)
                        .clickable { /* Upload image */ },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(SurfaceVariant)
                                .shadow(1.dp, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Primary)
                        }
                        Spacer(modifier = Modifier.height(Dimens.SpacingSm))
                        Text("Pilih Cover Image", style = LabelMd, color = OnSurfaceVariant)
                        Text("Rekomendasi ukuran 1200 x 600px (Max 5MB)", style = BodySm, color = Outline, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = Dimens.SpacingMd))
                    }
                }
            }
            Spacer(modifier = Modifier.height(Dimens.SpacingXl))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCommunityStep2TopBar() {
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
                Icon(Icons.Default.Person, contentDescription = null, tint = OnSurfaceVariant)
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

@Composable
fun CreateCommunityStep2BottomBar(onNavigateForward: () -> Unit) {
    Surface(
        color = Surface.copy(alpha = 0.95f),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.padding(horizontal = Dimens.ContainerPadding, vertical = Dimens.SpacingMd)
        ) {
            Button(
                onClick = onNavigateForward,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .shadow(4.dp, Shapes.Full),
                shape = Shapes.Full,
                colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnPrimary)
            ) {
                Text("Buat Sekarang", style = LabelMd)
                Spacer(modifier = Modifier.width(Dimens.SpacingSm))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
            }
        }
    }
}
