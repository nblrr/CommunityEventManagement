package com.example.communityeventmanagement.ui.screens.event

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.communityeventmanagement.data.model.*
import com.example.communityeventmanagement.data.repository.AppState
import com.example.communityeventmanagement.util.ImagePickerBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    communityId: Int,
    onCreateSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val community = AppState.communities.find { it.id == communityId }
    val isFormValid = title.isNotBlank() && description.isNotBlank() && date.isNotBlank() && location.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buat Event", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Nama Event *") }, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Deskripsi Event *") }, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth(), minLines = 3)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Tanggal *") },
                placeholder = { Text("Contoh: 25 4 2026 (Tgl Bln Thn)") },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Lokasi *") }, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(16.dp))

            Text(text = "Gambar Event", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            ImagePickerBox(
                imageUri = imageUrl.ifBlank { null },
                onImageSelected = { imageUrl = it ?: "" },
                label = "Pilih Gambar Event"
            )

            Spacer(Modifier.height(28.dp))
            Button(
                onClick = {
                    val newEventId = (AppState.communities.flatMap { it.events }.maxOfOrNull { it.id } ?: 0) + 1
                    val newEvent = Event(
                        id = newEventId,
                        title = title.trim(),
                        description = description.trim(),
                        date = date.trim(),
                        location = location.trim(),
                        category = community?.category ?: "General",
                        coverImageUri = imageUrl.ifBlank { null },
                        communityId = communityId,
                        registeredUserIds = mutableListOf()
                    )
                    val index = AppState.communities.indexOfFirst { it.id == communityId }
                    if (index != -1) {
                        AppState.communities[index] = AppState.communities[index].copy(events = AppState.communities[index].events + newEvent)
                        AppState.saveCommunityData()
                    }
                    showSuccessDialog = true
                },
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Buat Event", fontWeight = FontWeight.ExtraBold)
            }
            Spacer(Modifier.height(32.dp))
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Event Berhasil Dibuat!", fontWeight = FontWeight.ExtraBold) },
            text = { Text("\"$title\" sudah aktif. Anggota sekarang bisa melihat dan mendaftar!") },
            confirmButton = {
                Button(onClick = { onCreateSuccess() }) { Text("Selesai") }
            }
        )
    }
}
