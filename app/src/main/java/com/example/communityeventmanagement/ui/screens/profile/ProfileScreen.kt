package com.example.communityeventmanagement.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.communityeventmanagement.data.model.*
import com.example.communityeventmanagement.data.repository.AppState
import com.example.communityeventmanagement.ui.components.CommunityCard
import com.example.communityeventmanagement.util.CoverImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    currentUser: UserProfile?,
    onNavigateToOrganizerRegister: () -> Unit,
    onNavigateToCommunityDetail: (Int) -> Unit,
    onLogout: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showTrustedAppSheet by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val allCommunities = AppState.communities
    val joinedCommunities = allCommunities.filter { it.id in AppState.joinedCommunityIds }
    val createdCommunities = allCommunities.filter { it.organizerId == currentUser?.id }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Profil", fontWeight = FontWeight.Black) },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Header Profil
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier.size(100.dp).clip(CircleShape).background(Brush.linearGradient(colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))).padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize().clip(CircleShape).background(MaterialTheme.colorScheme.surface),
                                contentAlignment = Alignment.Center
                            ) {
                                CoverImage(
                                    imageUri = currentUser?.avatarUri,
                                    modifier = Modifier.fillMaxSize(),
                                    placeholder = {
                                        Text(
                                            text = currentUser?.name?.take(1)?.uppercase() ?: "?",
                                            style = MaterialTheme.typography.headlineLarge,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                )
                            }
                        }
                        
                        Surface(
                            shape = CircleShape, color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp), shadowElevation = 4.dp
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.padding(8.dp), tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    Text(text = currentUser?.name ?: "Pengguna", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                    Text(text = currentUser?.email ?: "-", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // Badge Role
                    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), 
                            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                if (currentUser?.isTrusted == true) Icons.Default.Verified 
                                else if (currentUser?.role == "Admin") Icons.Default.AdminPanelSettings
                                else Icons.Default.Person,
                                contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (currentUser?.isTrusted == true) "Trusted Organizer" else (currentUser?.role ?: "Member"),
                                style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            // Statistik
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatBox(label = "Diikuti", value = "${joinedCommunities.size}", icon = Icons.Default.Groups, modifier = Modifier.weight(1f))
                    StatBox(label = "Dibuat", value = "${createdCommunities.size}", icon = Icons.Default.AddCircle, modifier = Modifier.weight(1f))
                }
            }

            // Menu Pengaturan
            item {
                Column(modifier = Modifier.padding(top = 24.dp)) {
                    Text("Pengaturan Akun", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
                    
                    if (currentUser?.role == "User") {
                        ProfileMenuItem(icon = Icons.Default.Star, title = "Daftar Jadi Organizer", subtitle = "Kelola komunitas sendiri", color = MaterialTheme.colorScheme.primary, onClick = onNavigateToOrganizerRegister)
                    }

                    if (currentUser?.role == "Organizer" && !currentUser.isTrusted && currentUser.trustedAppStatus != "PENDING") {
                        ProfileMenuItem(icon = Icons.Default.VerifiedUser, title = "Ajukan Trusted Organizer", subtitle = "Dapatkan lencana verifikasi", color = Color(0xFF3B82F6), onClick = { showTrustedAppSheet = true })
                    } else if (currentUser?.trustedAppStatus == "PENDING") {
                        ProfileMenuItem(icon = Icons.Default.HourglassBottom, title = "Verifikasi Diproses", subtitle = "Sedang ditinjau admin", color = Color.Gray, onClick = {})
                    }

                    ProfileMenuItem(icon = Icons.Default.Settings, title = "Preferensi", subtitle = "Notifikasi & Tema", color = MaterialTheme.colorScheme.outline, onClick = {})
                }
            }

            // Komunitas Saya
            if (joinedCommunities.isNotEmpty()) {
                item {
                    Text("Komunitas Saya", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp))
                }
                items(joinedCommunities) { community ->
                    Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)) {
                        CommunityCard(community = community, isJoined = true, onClick = { onNavigateToCommunityDetail(community.id) })
                    }
                }
            }
        }
    }

    // Form Pengajuan (Bottom Sheet)
    if (showTrustedAppSheet) {
        var reason by remember { mutableStateOf("") }
        var experience by remember { mutableStateOf("") }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { showTrustedAppSheet = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Text("Ajukan Trusted Organizer", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                OutlinedTextField(value = reason, onValueChange = { reason = it }, label = { Text("Alasan ingin jadi trusted?") }, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth(), minLines = 3)
                OutlinedTextField(value = experience, onValueChange = { experience = it }, label = { Text("Pengalaman kelola komunitas") }, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth(), minLines = 3)
                Button(
                    onClick = {
                        AppState.submitTrustedApplication(reason, experience)
                        showTrustedAppSheet = false
                        scope.launch { snackbarHostState.showSnackbar("Pengajuan dikirim!") }
                    },
                    enabled = reason.isNotBlank() && experience.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp)
                ) { Text("Kirim Pengajuan", fontWeight = FontWeight.Black) }
            }
        }
    }

    // Keluar (Dialog)
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Keluar Akun?", fontWeight = FontWeight.Black) },
            text = { Text("Yakin ingin keluar sesi?") },
            confirmButton = {
                Button(onClick = { AppState.logout(); onLogout() }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Keluar") }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Batal") } },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

// Box Statistik
@Composable
private fun StatBox(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier, shape = RoundedCornerShape(24.dp), 
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        }
    }
}

// Menu Profil
@Composable
private fun ProfileMenuItem(icon: ImageVector, title: String, subtitle: String, color: Color, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = Modifier.fillMaxWidth(), color = Color.Transparent) {
        Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(color.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}
