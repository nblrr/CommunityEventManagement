package com.example.communityeventmanagement.ui.screens.event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.communityeventmanagement.data.model.*
import com.example.communityeventmanagement.data.repository.AppState
import com.example.communityeventmanagement.util.CoverImage
import com.example.communityeventmanagement.util.DateFormatter
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: Int,
    communityId: Int,
    currentUser: UserProfile?,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    // Find the community and event reactively
    val community = AppState.communities.find { it.id == communityId }
    val event = community?.events?.find { it.id == eventId }
    
    if (community == null || event == null) {
        return
    }

    val isOrganizer = currentUser?.id == community.organizerId
    val isRegistered = eventId in AppState.registeredEventIds

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { AppState.addGalleryImage(communityId, eventId, it.toString()) }
    }

    val isUpcoming = AppState.isUpcoming(event.date)
    val displayTitle = event.title.replace("Event Lampau \\d+ - ".toRegex(), "")
        .replace("Event Mendatang \\d+ - ".toRegex(), "")

    Scaffold(
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
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState())
        ) {
            CoverImage(
                imageUri = event.coverImageUri,
                modifier = Modifier.fillMaxWidth().height(200.dp),
                placeholder = {
                    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.White)
                    }
                }
            )

            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = displayTitle, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp)) {
                        Text(text = event.category, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(text = "di ${community.name}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                }

                Spacer(Modifier.height(24.dp))
                InfoRow(icon = Icons.Default.CalendarToday, text = DateFormatter.formatEventDate(event.date))
                Spacer(Modifier.height(12.dp))
                if (event.time.isNotBlank()) {
                    InfoRow(icon = Icons.Default.Schedule, text = event.time)
                    Spacer(Modifier.height(12.dp))
                }
                InfoRow(icon = Icons.Default.LocationOn, text = event.location)

                Spacer(Modifier.height(24.dp))
                Text(text = "Tentang Event", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(text = event.description, style = MaterialTheme.typography.bodyLarge, lineHeight = 24.sp)

                // --- PHOTO GALLERY ---
                val gallery = event.galleryImages ?: emptyList()
                if (gallery.isNotEmpty() || isOrganizer) {
                    Spacer(Modifier.height(32.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Galeri Dokumentasi", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        if (isOrganizer) {
                            TextButton(onClick = { galleryLauncher.launch("image/*") }) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Tambah Foto")
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    
                    if (gallery.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(100.dp).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Belum ada dokumentasi.", color = MaterialTheme.colorScheme.outline)
                        }
                    } else {
                        androidx.compose.foundation.lazy.LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(end = 20.dp)
                        ) {
                            items(gallery.size) { index ->
                                Card(
                                    modifier = Modifier.size(140.dp, 100.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    CoverImage(
                                        imageUri = gallery[index],
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }

                // --- RATINGS & REVIEWS ---
                val ratings = event.ratings ?: emptyList()
                Spacer(Modifier.height(32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Ulasan & Rating", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    if (ratings.isNotEmpty()) {
                        val avg = ratings.map { it.score }.average()
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(18.dp))
                            Text(text = "%.1f".format(avg), fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                }
                
                if (ratings.isEmpty()) {
                    Text(text = "Belum ada ulasan.", color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 8.dp))
                } else {
                    Spacer(Modifier.height(12.dp))
                    ratings.forEach { rating ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = rating.userName, fontWeight = FontWeight.Bold)
                                    Row {
                                        repeat(rating.score) {
                                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                                Text(text = rating.comment, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
                                Text(text = rating.date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                }

                // --- ADD REVIEW FORM (only if finished) ---
                if (!isUpcoming && currentUser != null && isRegistered) {
                    val alreadyReviewed = ratings.any { it.userId == currentUser.id }
                    if (!alreadyReviewed) {
                        var reviewText by remember { mutableStateOf("") }
                        var selectedScore by remember { mutableIntStateOf(5) }
                        
                        Spacer(Modifier.height(24.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Berikan Ulasan Anda", fontWeight = FontWeight.Bold)
                                Row(modifier = Modifier.padding(vertical = 8.dp)) {
                                    repeat(5) { i ->
                                        IconButton(onClick = { selectedScore = i + 1 }, modifier = Modifier.size(32.dp)) {
                                            Icon(
                                                Icons.Default.Star, 
                                                contentDescription = null,
                                                tint = if (i < selectedScore) Color(0xFFFFD700) else Color.Gray.copy(alpha = 0.5f)
                                            )
                                        }
                                    }
                                }
                                OutlinedTextField(
                                    value = reviewText,
                                    onValueChange = { reviewText = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Tulis komentar...") },
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Button(
                                    onClick = {
                                        AppState.addEventRating(communityId, eventId, selectedScore, reviewText)
                                        reviewText = ""
                                    },
                                    modifier = Modifier.padding(top = 12.dp).align(Alignment.End),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Kirim Ulasan")
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
                if (isUpcoming) {
                    Button(
                        onClick = {
                            if (currentUser == null) onNavigateToLogin()
                            else {
                                AppState.toggleEventRegistration(communityId, eventId)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isRegistered) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(if (isRegistered) Icons.Default.PersonRemove else Icons.Default.PersonAdd, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(text = if (isRegistered) "Batalkan Pendaftaran" else "Daftar Event Sekarang", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (isRegistered) MaterialTheme.colorScheme.error else Color.White)
                    }
                } else {
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "Event telah berakhir", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                val attendeeCount = event.attendeeCount
                Text(text = "$attendeeCount orang sudah mendaftar", modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)

                if (event.registeredUserIds.isNotEmpty()) {
                    Spacer(Modifier.height(32.dp))
                    Text(text = "Daftar Peserta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    
                    event.registeredUserIds.take(10).forEach { userId ->
                        val user = AppState.allUsers.find { it.id == userId }
                        if (user != null) {
                            Row(
                                modifier = Modifier.padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(user.name.take(1).uppercase(), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.width(12.dp))
                                Text(user.name, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                    if (event.registeredUserIds.size > 10) {
                        Text(
                            text = "+ ${event.registeredUserIds.size - 10} lainnya",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}
