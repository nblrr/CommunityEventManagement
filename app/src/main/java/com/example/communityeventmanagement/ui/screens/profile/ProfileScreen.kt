package com.example.communityeventmanagement.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    currentUser: UserProfile?,
    onNavigateBack: () -> Unit,
    onNavigateToOrganizerRegister: () -> Unit,
    onNavigateToCommunityDetail: (Int) -> Unit,
    onLogout: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showTrustedAppDialog by remember { mutableStateOf(false) }
    
    val allCommunities = AppState.communities
    val joinedCommunities = allCommunities.filter { it.id in AppState.joinedCommunityIds }
    val createdCommunities = allCommunities.filter { it.organizerId == currentUser?.id }
    val joinedCount = AppState.joinedCommunityIds.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Saya", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp).clip(RoundedCornerShape(24.dp)).background(Brush.linearGradient(colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))).padding(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.25f)), contentAlignment = Alignment.Center) {
                        CoverImage(
                            imageUri = currentUser?.avatarUri,
                            modifier = Modifier.fillMaxSize(),
                            placeholder = {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(text = currentUser?.name?.take(2)?.uppercase() ?: "??", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                }
                            }
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(text = currentUser?.name ?: "Pengguna", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text(text = currentUser?.email ?: "-", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.75f))
                    Spacer(Modifier.height(16.dp))

                    Surface(shape = RoundedCornerShape(20.dp), color = Color.White.copy(alpha = 0.2f)) {
                        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (currentUser?.isTrusted == true) {
                                Icon(Icons.Default.Verified, contentDescription = "Trusted", modifier = Modifier.size(16.dp), tint = Color.White)
                            } else {
                                Icon(if (currentUser?.role == "Admin") Icons.Default.AdminPanelSettings else if (currentUser?.role == "Organizer") Icons.Default.Star else Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            }
                            Text(text = if (currentUser?.isTrusted == true) "Trusted Organizer" else (currentUser?.role ?: "Member"), style = MaterialTheme.typography.labelLarge, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(label = "Komunitas", value = joinedCount.toString(), icon = Icons.Default.Groups, modifier = Modifier.weight(1f))
                StatCard(label = "Role", value = currentUser?.role ?: "Member", icon = if (currentUser?.role == "Admin") Icons.Default.AdminPanelSettings else Icons.Default.Person, modifier = Modifier.weight(1f))
            }

            if (joinedCommunities.isNotEmpty()) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    Text(text = "Komunitas yang Diikuti", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(bottom = 10.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        joinedCommunities.forEach { community ->
                            CommunityCard(
                                community = community,
                                isJoined = true,
                                onClick = { onNavigateToCommunityDetail(community.id) }
                            )
                        }
                    }
                }
            }

            if (createdCommunities.isNotEmpty()) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    Text(text = "Komunitas yang Dibuat", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(bottom = 10.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        createdCommunities.forEach { community ->
                            CommunityCard(
                                community = community,
                                isJoined = community.id in AppState.joinedCommunityIds,
                                onClick = { onNavigateToCommunityDetail(community.id) }
                            )
                        }
                    }
                }
            }

            if (currentUser?.role == "Organizer" && currentUser.organizerProfile != null) {
                val op = currentUser.organizerProfile
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(text = "Info Organizer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(bottom = 10.dp))
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            OrganizerInfoRow("Nama Organizer", op.communityName, Icons.Default.Groups)
                            Spacer(Modifier.height(8.dp))
                            OrganizerInfoRow("No. Telepon", op.phone, Icons.Default.Phone)
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(text = "Pengaturan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 16.dp, bottom = 10.dp))
                
                if (currentUser?.role == "User") {
                    ProfileMenuCard(icon = Icons.Default.Star, title = "Daftar Jadi Organizer", subtitle = "Buat dan kelola komunitas & event kamu", iconColor = MaterialTheme.colorScheme.tertiary, onClick = onNavigateToOrganizerRegister)
                }
                
                if (currentUser?.role == "Organizer" && !currentUser.isTrusted && currentUser.trustedAppStatus != "PENDING") {
                    ProfileMenuCard(icon = Icons.Default.VerifiedUser, title = "Ajukan Trusted Organizer", subtitle = "Dapatkan badge terverifikasi & fitur eksklusif", iconColor = MaterialTheme.colorScheme.primary, onClick = { showTrustedAppDialog = true })
                } else if (currentUser?.trustedAppStatus == "PENDING") {
                    ProfileMenuCard(icon = Icons.Default.HourglassBottom, title = "Pengajuan Sedang Ditinjau", subtitle = "Admin akan segera memeriksa pengajuanmu", iconColor = Color.Gray, onClick = { })
                }

                ProfileMenuCard(icon = Icons.AutoMirrored.Filled.Logout, title = "Keluar", subtitle = "Logout dari akun ini", iconColor = MaterialTheme.colorScheme.error, onClick = { showLogoutDialog = true })
            }
            Spacer(Modifier.height(32.dp))
        }
    }

    if (showTrustedAppDialog) {
        var reason by remember { mutableStateOf("") }
        var experience by remember { mutableStateOf("") }
        
        AlertDialog(
            onDismissRequest = { showTrustedAppDialog = false },
            title = { Text("Ajukan Trusted Status", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Berikan alasan dan pengalaman Anda dalam mengelola komunitas.", style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Alasan Pengajuan") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = experience,
                        onValueChange = { experience = it },
                        label = { Text("Pengalaman Mengelola") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        AppState.submitTrustedApplication(reason, experience)
                        showTrustedAppDialog = false
                    },
                    enabled = reason.isNotBlank() && experience.isNotBlank()
                ) { Text("Kirim Pengajuan") }
            },
            dismissButton = { TextButton(onClick = { showTrustedAppDialog = false }) { Text("Batal") } }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Keluar?", fontWeight = FontWeight.ExtraBold) },
            text = { Text("Kamu akan logout dari akun ini. Yakin?") },
            confirmButton = {
                TextButton(onClick = { 
                    AppState.logout()
                    onLogout() 
                }) { Text("Keluar", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
private fun StatCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f))
        }
    }
}

@Composable
private fun OrganizerInfoRow(label: String, value: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f))
            Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ProfileMenuCard(icon: ImageVector, title: String, subtitle: String, iconColor: Color, onClick: () -> Unit) {
    Card(onClick = onClick, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(iconColor.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f))
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(20.dp))
        }
    }
}
