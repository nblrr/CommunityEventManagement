package com.example.communityeventmanagement.ui.screens.event

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.communityeventmanagement.data.model.UserProfile
import com.example.communityeventmanagement.data.repository.AppState
import com.example.communityeventmanagement.ui.components.StatusBadge
import com.example.communityeventmanagement.util.CoverImage
import com.example.communityeventmanagement.util.DateFormatter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: Int,
    communityId: Int,
    currentUser: UserProfile?,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val community = AppState.communities.find { it.id == communityId }
    val event = community?.events?.find { it.id == eventId }

    if (community == null || event == null) return

    val isOrganizer = currentUser?.id == community.organizerId
    val isRegistered = eventId in AppState.registeredEventIds
    val isUpcoming = AppState.isUpcoming(event.date)
    val ratings = event.ratings ?: emptyList()
    val gallery = event.galleryImages ?: emptyList()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showReviewSheet by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            AppState.addGalleryImage(communityId, eventId, it.toString())
            scope.launch { snackbarHostState.showSnackbar("Foto berhasil ditambahkan ke galeri.") }
        }
    }

    val displayTitle = event.title
        .replace("Event Lampau \\d+ - ".toRegex(), "")
        .replace("Event Mendatang \\d+ - ".toRegex(), "")

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detail Event", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Cover image
            item {
                CoverImage(
                    imageUri = event.coverImageUri,
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    placeholder = {
                        Box(
                            modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.White)
                        }
                    }
                )
            }

            // Title & category
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(text = displayTitle, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
                        StatusBadge(text = if (isUpcoming) "Mendatang" else "Selesai", isActive = isUpcoming)
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp)) {
                            Text(text = event.category, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(text = "di ${community.name}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }

            // Info rows
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    InfoRow(Icons.Default.CalendarToday, DateFormatter.formatEventDate(event.date))
                    if (event.time.isNotBlank()) {
                        Spacer(Modifier.height(12.dp))
                        InfoRow(Icons.Default.Schedule, event.time)
                    }
                    Spacer(Modifier.height(12.dp))
                    InfoRow(Icons.Default.LocationOn, event.location)
                }
            }

            // Description
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Text(text = "Tentang Event", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(text = event.description, style = MaterialTheme.typography.bodyLarge, lineHeight = 24.sp)
                }
            }

            // Register / cancel button
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    if (isUpcoming) {
                        Button(
                            onClick = {
                                if (currentUser == null) onNavigateToLogin()
                                else if (isRegistered) showCancelDialog = true
                                else {
                                    AppState.toggleEventRegistration(communityId, eventId)
                                    scope.launch { snackbarHostState.showSnackbar("Berhasil mendaftar event!") }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRegistered) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primary,
                                contentColor = if (isRegistered) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(if (isRegistered) Icons.Default.EventBusy else Icons.Default.EventAvailable, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(if (isRegistered) "Batalkan Pendaftaran" else "Daftar Event Sekarang", fontWeight = FontWeight.Bold)
                        }
                    } else if (isRegistered && ratings.none { it.userId == currentUser?.id }) {
                        Button(
                            onClick = { showReviewSheet = true },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Beri Rating & Ulasan", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Gallery section
            if (isOrganizer || gallery.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(top = 24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Galeri Event", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            if (isOrganizer) {
                                TextButton(onClick = { galleryLauncher.launch("image/*") }) {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Tambah Foto")
                                }
                            }
                        }
                        
                        if (gallery.isEmpty()) {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("Belum ada foto galeri.", color = MaterialTheme.colorScheme.outline)
                            }
                        } else {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                items(gallery) { uri ->
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.size(120.dp)
                                    ) {
                                        CoverImage(imageUri = uri, modifier = Modifier.fillMaxSize())
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Ratings section
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
                    Text(text = "Ulasan Anggota", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    
                    if (ratings.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                            Text("Belum ada ulasan untuk event ini.", color = MaterialTheme.colorScheme.outline)
                        }
                    } else {
                        ratings.forEach { rating ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = rating.userName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                        Spacer(Modifier.weight(1f))
                                        repeat(5) { index ->
                                            Icon(
                                                Icons.Default.Star,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp),
                                                tint = if (index < rating.score) Color(0xFFFFB100) else Color.LightGray
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(text = rating.date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                    Spacer(Modifier.height(8.dp))
                                    Text(text = rating.comment, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Cancel registration dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Batalkan Pendaftaran?") },
            text = { Text("Apakah Anda yakin ingin membatalkan pendaftaran untuk event ini?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        AppState.toggleEventRegistration(communityId, eventId)
                        showCancelDialog = false
                        scope.launch { snackbarHostState.showSnackbar("Pendaftaran dibatalkan.") }
                    }
                ) { Text("Ya, Batalkan", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) { Text("Kembali") }
            }
        )
    }

    // Review bottom sheet
    if (showReviewSheet) {
        var score by remember { mutableIntStateOf(5) }
        var comment by remember { mutableStateOf("") }
        val sheetState = rememberModalBottomSheetState()

        ModalBottomSheet(
            onDismissRequest = { showReviewSheet = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Bagaimana event ini?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                Row {
                    repeat(5) { index ->
                        IconButton(onClick = { score = index + 1 }) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = if (index < score) Color(0xFFFFB100) else Color.LightGray
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Tulis ulasan Anda...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3
                )
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = {
                        AppState.addEventRating(communityId, eventId, score, comment)
                        showReviewSheet = false
                        scope.launch { snackbarHostState.showSnackbar("Terima kasih atas ulasan Anda!") }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    enabled = comment.isNotBlank()
                ) {
                    Text("Kirim Ulasan", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}
