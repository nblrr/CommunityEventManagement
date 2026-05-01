package com.example.communityeventmanagement.ui.screens.event

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.communityeventmanagement.data.model.*
import com.example.communityeventmanagement.data.repository.AppState
import com.example.communityeventmanagement.util.DatePickerField
import com.example.communityeventmanagement.util.ImagePickerBox
import com.example.communityeventmanagement.util.toDateString
import com.example.communityeventmanagement.util.toDisplayDateString
import kotlinx.coroutines.launch

private val timeSlots = listOf(
    "07.00", "08.00", "09.00", "10.00", "11.00", "12.00",
    "13.00", "14.00", "15.00", "16.00", "17.00", "18.00", "19.00", "20.00"
)

private val categoryOptions = listOf(
    "Technology", "Design", "Business", "Education",
    "Health", "Art", "Music", "Sports", "Social"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    communityId: Int,
    onCreateSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var selectedTime by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var showSuccessSheet by remember { mutableStateOf(false) }

    val community = AppState.communities.find { it.id == communityId }
    var selectedCategory by remember { mutableStateOf(community?.category ?: "General") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val isFormValid = title.isNotBlank() && description.isNotBlank() &&
            selectedDateMillis != null && location.isNotBlank()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Nama Event *") },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Event, contentDescription = null) }
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Deskripsi Event *") },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(Modifier.height(12.dp))

            DatePickerField(
                label = "Tanggal Event *",
                selectedDateMillis = selectedDateMillis,
                onDateSelected = { selectedDateMillis = it },
                modifier = Modifier.fillMaxWidth()
            )
            if (selectedDateMillis != null) {
                Text(
                    text = selectedDateMillis!!.toDisplayDateString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
            Spacer(Modifier.height(12.dp))

            Text(
                text = "Pilih Waktu",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(timeSlots) { time ->
                    FilterChip(
                        selected = time == selectedTime,
                        onClick = { selectedTime = if (selectedTime == time) "" else time },
                        label = { Text(time) }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Lokasi *") },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) }
            )
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Kategori Event",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categoryOptions) { category ->
                    FilterChip(
                        selected = category == selectedCategory,
                        onClick = { selectedCategory = category },
                        label = { Text(category) }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Gambar Event",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ImagePickerBox(
                imageUri = imageUrl.ifBlank { null },
                onImageSelected = { imageUrl = it ?: "" },
                label = "Pilih Gambar Event"
            )

            Spacer(Modifier.height(28.dp))
            Button(
                onClick = {
                    val dateString = selectedDateMillis!!.toDateString()
                    val newEventId = (AppState.communities.flatMap { it.events }.maxOfOrNull { it.id } ?: 0) + 1
                    val newEvent = Event(
                        id = newEventId,
                        title = title.trim(),
                        description = description.trim(),
                        date = dateString,
                        time = selectedTime,
                        location = location.trim(),
                        category = selectedCategory,
                        coverImageUri = imageUrl.ifBlank { null },
                        communityId = communityId,
                        registeredUserIds = emptyList()
                    )
                    val index = AppState.communities.indexOfFirst { it.id == communityId }
                    if (index != -1) {
                        AppState.communities[index] = AppState.communities[index].copy(
                            events = AppState.communities[index].events + newEvent
                        )
                        AppState.saveCommunityData()
                        showSuccessSheet = true
                    } else {
                        scope.launch { snackbarHostState.showSnackbar("Gagal membuat event. Coba lagi.") }
                    }
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

    if (showSuccessSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { onCreateSuccess() },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(56.dp))
                Text("Event Berhasil Dibuat!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Text(
                    "\"$title\" sudah aktif. Anggota komunitas sekarang bisa melihat dan mendaftar!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = { onCreateSuccess() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Selesai", fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}