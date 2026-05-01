package com.example.communityeventmanagement.ui.screens.admin

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.communityeventmanagement.data.model.UserProfile
import com.example.communityeventmanagement.data.repository.AppState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var userToToggleBlock by remember { mutableStateOf<UserProfile?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val users = AppState.allUsers.filter { user ->
        (if (selectedTab == 1) user.role == "Organizer" else true) &&
                (user.name.contains(searchQuery, ignoreCase = true) || user.email.contains(searchQuery, ignoreCase = true))
    }.sortedBy { it.role }

    val pendingApps = AppState.trustedApplications

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Admin Panel", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
            // Cari data
            if (selectedTab != 2) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Cari user atau organizer...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(Modifier.height(16.dp))
            }

            // Tab Menu
            SecondaryTabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("User", modifier = Modifier.padding(vertical = 12.dp))
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("Organizer", modifier = Modifier.padding(vertical = 12.dp))
                }
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                    BadgedBox(badge = { if (pendingApps.isNotEmpty()) Badge { Text(pendingApps.size.toString()) } }) {
                        Text("Pengajuan", modifier = Modifier.padding(vertical = 12.dp))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Daftar data
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                if (selectedTab == 2) {
                    items(pendingApps, key = { it.userId }) { application ->
                        TrustedAppCard(
                            application = application,
                            onApprove = {
                                AppState.handleTrustedApplication(application.userId, true)
                                scope.launch { snackbarHostState.showSnackbar("Pengajuan ${application.userName} disetujui.") }
                            },
                            onReject = {
                                AppState.handleTrustedApplication(application.userId, false)
                                scope.launch { snackbarHostState.showSnackbar("Pengajuan ${application.userName} ditolak.") }
                            }
                        )
                    }
                    if (pendingApps.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("Tidak ada pengajuan baru.", color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                } else {
                    items(users, key = { it.id }) { user ->
                        UserManagementCard(
                            user = user,
                            onBlockToggle = { userToToggleBlock = user }
                        )
                    }
                    if (users.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("Tidak ada data ditemukan.", color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog Blokir
    userToToggleBlock?.let { user ->
        AlertDialog(
            onDismissRequest = { userToToggleBlock = null },
            title = { Text(if (user.isBlocked) "Buka Blokir User?" else "Blokir User?") },
            text = { Text("Apakah Anda yakin ingin ${if (user.isBlocked) "membuka blokir" else "memblokir"} akun ${user.name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val index = AppState.allUsers.indexOfFirst { it.id == user.id }
                        if (index != -1) {
                            val wasBlocked = user.isBlocked
                            val updatedUser = user.copy(isBlocked = !wasBlocked)
                            AppState.allUsers[index] = updatedUser
                            
                            // Also update current user if they are the one being blocked/unblocked
                            if (AppState.currentUser?.id == user.id) {
                                AppState.currentUser = updatedUser
                            }

                            AppState.saveUserData()
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    if (wasBlocked) "${user.name} berhasil dibuka blokirnya."
                                    else "${user.name} berhasil diblokir."
                                )
                            }
                        }
                        userToToggleBlock = null
                    }
                ) {
                    Text(if (user.isBlocked) "Buka Blokir" else "Blokir", color = if (user.isBlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { userToToggleBlock = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

// Card Pengajuan
@Composable
fun TrustedAppCard(
    application: com.example.communityeventmanagement.data.model.TrustedApplication,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = application.userName, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
            Text(text = "Komunitas: ${application.communityName}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(text = "Alasan:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            Text(text = application.reason, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text(text = "Pengalaman:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            Text(text = application.experience, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onApprove, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) {
                    Text("Terima")
                }
                OutlinedButton(
                    onClick = onReject, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Tolak")
                }
            }
        }
    }
}

// Card Kelola User
@Composable
fun UserManagementCard(user: UserProfile, onBlockToggle: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (user.isBlocked) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(user.name.take(1).uppercase(), fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(user.name, fontWeight = FontWeight.Bold)
                    if (user.isTrusted) {
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                    }
                }
                Text(user.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SuggestionChip(onClick = {}, label = { Text(user.role, style = MaterialTheme.typography.labelSmall) })
                    if (user.isBlocked) {
                        Spacer(Modifier.width(8.dp))
                        Text("TERBLOKIR", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
            IconButton(onClick = onBlockToggle) {
                Icon(
                    if (user.isBlocked) Icons.Default.LockOpen else Icons.Default.Block,
                    contentDescription = if (user.isBlocked) "Buka Blokir" else "Blokir",
                    tint = if (user.isBlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
