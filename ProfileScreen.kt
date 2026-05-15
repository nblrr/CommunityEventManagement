package com.example.communityeventmanagement.features.profile

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.data.model.Community
import com.example.communityeventmanagement.data.model.UserProfile
import com.example.communityeventmanagement.ui.AppViewModelProvider
import com.example.communityeventmanagement.ui.components.CommunityCard
import com.example.communityeventmanagement.ui.theme.CommunityEventManagementTheme
import com.example.communityeventmanagement.ui.theme.ThemePreviews
import com.example.communityeventmanagement.util.ImagePickerBox
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    currentUser: UserProfile?,
    onNavigateToOrganizerRegister: () -> Unit,
    onNavigateToCommunityDetail: (Int) -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val joinedCommunities = viewModel.joinedCommunities
    val managedCommunities = viewModel.managedCommunities
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val applicationSentMsg = stringResource(R.string.msg_application_sent)

    ProfileContent(
        currentUser = currentUser,
        joinedCommunities = joinedCommunities,
        managedCommunities = managedCommunities,
        currentThemeMode = viewModel.currentThemeMode,
        snackbarHostState = snackbarHostState,
        onUpdateAvatar = { viewModel.updateAvatar(it) },
        onSubmitTrustedApplication = { reason, exp ->
            viewModel.submitTrustedApplication(reason, exp)
            scope.launch { snackbarHostState.showSnackbar(applicationSentMsg) }
        },
        onSaveTheme = { viewModel.saveTheme(it) },
        onLogoutClick = {
            viewModel.logout()
            onLogout()
        },
        onNavigateToOrganizerRegister = onNavigateToOrganizerRegister,
        onNavigateToCommunityDetail = onNavigateToCommunityDetail
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    currentUser: UserProfile?,
    joinedCommunities: List<Community>,
    managedCommunities: List<Community>,
    currentThemeMode: String,
    snackbarHostState: SnackbarHostState,
    onUpdateAvatar: (String?) -> Unit,
    onSubmitTrustedApplication: (String, String) -> Unit,
    onSaveTheme: (String) -> Unit,
    onLogoutClick: () -> Unit,
    onNavigateToOrganizerRegister: () -> Unit,
    onNavigateToCommunityDetail: (Int) -> Unit,
) {
    var showLogoutDialog by remember { mutableStateOf(value = false) }
    var showTrustedAppSheet by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_profile), fontWeight = FontWeight.Black) },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = stringResource(R.string.cd_logout), tint = MaterialTheme.colorScheme.error)
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
            // User Profile Section
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ImagePickerBox(
                        imageUri = currentUser?.avatarUri,
                        onImageSelected = onUpdateAvatar,
                        isProfile = true,
                        userName = currentUser?.name ?: "",
                        height = 100.dp,
                        modifier = Modifier.size(100.dp)
                    )
                    
                    Spacer(Modifier.height(16.dp))
                    Text(text = currentUser?.name ?: stringResource(R.string.label_user_default), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                    Text(text = currentUser?.email ?: "-", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // User Badge Section
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
                                text = if (currentUser?.isTrusted == true) stringResource(R.string.role_trusted_organizer) else (currentUser?.role ?: stringResource(R.string.role_member)),
                                style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            // Statistics Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatBox(label = stringResource(R.string.stat_followed_profile), value = joinedCommunities.size.toString(), icon = Icons.Default.Groups, modifier = Modifier.weight(1f))
                    StatBox(label = stringResource(R.string.stat_created), value = managedCommunities.size.toString(), icon = Icons.Default.AddCircle, modifier = Modifier.weight(1f))
                }
            }

            // Account Settings Menu
            item {
                Column(modifier = Modifier.padding(top = 24.dp)) {
                    Text(stringResource(R.string.section_account_settings), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
                    
                    if (currentUser?.role == "User") {
                        ProfileMenuItem(icon = Icons.Default.Star, title = stringResource(R.string.menu_become_organizer), subtitle = stringResource(R.string.menu_manage_own_community), color = MaterialTheme.colorScheme.primary, onClick = onNavigateToOrganizerRegister)
                    }

                    if ((currentUser?.role == "Organizer" && !currentUser.isTrusted) && currentUser.trustedApplicationStatus != "PENDING") {
                        ProfileMenuItem(icon = Icons.Default.VerifiedUser, title = stringResource(R.string.menu_apply_trusted), subtitle = stringResource(R.string.menu_get_verification_badge), color = Color(0xFF3B82F6), onClick = { showTrustedAppSheet = true })
                    } else if (currentUser?.trustedApplicationStatus == "PENDING") {
                        ProfileMenuItem(icon = Icons.Default.HourglassBottom, title = stringResource(R.string.menu_verification_status), subtitle = stringResource(R.string.menu_verification_in_progress), color = Color.Gray) {}
                    }

                    ProfileMenuItem(
                        icon = Icons.Default.Settings, 
                        title = stringResource(R.string.menu_preferences), 
                        subtitle = stringResource(R.string.menu_preferences_subtitle), 
                        color = MaterialTheme.colorScheme.outline,
                        onClick = { showThemeDialog = true }
                    )
                }
            }

            // My Communities Section
            if (joinedCommunities.isNotEmpty()) {
                item {
                    Text(stringResource(R.string.section_my_communities), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp))
                }
                items(joinedCommunities) { community ->
                    Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)) {
                        CommunityCard(community = community, isJoined = true, onClick = { onNavigateToCommunityDetail(community.id) })
                    }
                }
            }
        }
    }

    // Application Form (Bottom Sheet)
    if (showTrustedAppSheet) {
        var reason by remember { mutableStateOf("") }
        var experience by remember { mutableStateOf("") }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { showTrustedAppSheet = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Text(stringResource(R.string.menu_apply_trusted), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                OutlinedTextField(value = reason, onValueChange = { reason = it }, label = { Text(stringResource(R.string.label_reason_apply_trusted)) }, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth(), minLines = 3)
                OutlinedTextField(value = experience, onValueChange = { experience = it }, label = { Text(stringResource(R.string.label_experience_manage_community)) }, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth(), minLines = 3)
                Button(
                    onClick = {
                        onSubmitTrustedApplication(reason, experience)
                        showTrustedAppSheet = false
                    },
                    enabled = reason.isNotBlank() && experience.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp)
                ) { Text(stringResource(R.string.btn_send_application), fontWeight = FontWeight.Black) }
            }
        }
    }

    // Theme Picker Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text(stringResource(R.string.menu_app_theme), fontWeight = FontWeight.Black) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val options = listOf("AUTO", "LIGHT", "DARK")
                    options.forEach { option ->
                        val label = when(option) {
                            "LIGHT" -> stringResource(R.string.theme_light)
                            "DARK" -> stringResource(R.string.theme_dark)
                            else -> stringResource(R.string.theme_auto)
                        }
                        Surface(
                            onClick = { 
                                onSaveTheme(option)
                                showThemeDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = if (currentThemeMode == option) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = when(option) {
                                        "LIGHT" -> Icons.Default.LightMode
                                        "DARK" -> Icons.Default.DarkMode
                                        else -> Icons.Default.BrightnessAuto
                                    },
                                    contentDescription = null,
                                    tint = if (currentThemeMode == option) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = label, 
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (currentThemeMode == option) FontWeight.Bold else FontWeight.Normal,
                                    color = if (currentThemeMode == option) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.weight(1f))
                                if (currentThemeMode == option) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showThemeDialog = false }) { Text(stringResource(R.string.btn_close)) } },
            shape = RoundedCornerShape(28.dp)
        )
    }

    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(R.string.dialog_logout_title), fontWeight = FontWeight.Black) },
            text = { Text(stringResource(R.string.dialog_logout_msg)) },
            confirmButton = {
                Button(
                    onClick = { 
                        onLogoutClick()
                    }, 
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text(stringResource(R.string.btn_logout)) }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text(stringResource(R.string.btn_cancel)) } },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

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

@ThemePreviews
@Composable
fun ProfileScreenPreview() {
    CommunityEventManagementTheme {
        ProfileContent(
            currentUser = UserProfile(id = "1", name = "Budi Santoso", email = "budi@example.com", role = "Organizer", isTrusted = true),
            joinedCommunities = listOf(
                Community(1, "Android Dev", "Komunitas Android.", "Teknologi", null, "1", "Admin")
            ),
            managedCommunities = listOf(
                Community(1, "Android Dev", "Komunitas Android.", "Teknologi", null, "1", "Admin")
            ),
            currentThemeMode = "LIGHT",
            snackbarHostState = remember { SnackbarHostState() },
            onUpdateAvatar = {},
            onSubmitTrustedApplication = { _, _ -> },
            onSaveTheme = {},
            onLogoutClick = {},
            onNavigateToOrganizerRegister = {},
            onNavigateToCommunityDetail = {}
        )
    }
}
