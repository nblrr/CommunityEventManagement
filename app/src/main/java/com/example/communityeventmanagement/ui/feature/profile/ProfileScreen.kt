package com.example.communityeventmanagement.ui.feature.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.domain.model.ApplicationStatus
import com.example.communityeventmanagement.domain.model.Community
import com.example.communityeventmanagement.domain.model.ThemeMode
import com.example.communityeventmanagement.domain.model.User
import com.example.communityeventmanagement.domain.model.UserRole
import com.example.communityeventmanagement.ui.components.CommunityHorizontalCard
import com.example.communityeventmanagement.ui.components.StatusBadge
import com.example.communityeventmanagement.ui.components.glassmorphism
import com.example.communityeventmanagement.ui.theme.CommunityEventManagementTheme
import com.example.communityeventmanagement.ui.theme.ThemePreviews
import com.example.communityeventmanagement.util.AvatarImage

@Composable
fun ProfileScreen(
    onNavigateToOrganizerRegister: () -> Unit,
    onNavigateToTrustedApply: () -> Unit,
    onNavigateToCommunityDetail: (Int) -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val communities by viewModel.communities.collectAsStateWithLifecycle(initialValue = emptyList())
    val themeMode by viewModel.themeModeFlow.collectAsStateWithLifecycle()

    ProfileContent(
        user = user,
        communities = communities,
        themeMode = themeMode,
        onAvatarChange = viewModel::updateAvatar,
        onThemeChange = viewModel::saveTheme,
        onLogoutClick = { viewModel.logout(onLogout) },
        onNavigateToOrganizerRegister = onNavigateToOrganizerRegister,
        onNavigateToTrustedApply = onNavigateToTrustedApply,
        onNavigateToCommunityDetail = onNavigateToCommunityDetail,
        onNavigateToEditProfile = onNavigateToEditProfile
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    user: User?,
    communities: List<Community>,
    themeMode: ThemeMode,
    onAvatarChange: (String?) -> Unit,
    onThemeChange: (Int) -> Unit,
    onLogoutClick: () -> Unit,
    onNavigateToOrganizerRegister: () -> Unit,
    onNavigateToTrustedApply: () -> Unit,
    onNavigateToCommunityDetail: (Int) -> Unit,
    onNavigateToEditProfile: () -> Unit,
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.title_profile), style = MaterialTheme.typography.titleLarge) },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            Icons.AutoMirrored.Rounded.Logout,
                            contentDescription = stringResource(R.string.btn_logout),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Profile Header
            item {
                ProfileHeader(
                    user = user,
                    onAvatarClick = { onAvatarChange(null) }
                )
            }

            // Stats Section
            item {
                val followedCount = communities.count { it.memberIds.contains(user?.id) }
                ProfileStats(
                    communitiesCount = followedCount,
                    eventsCount = 0
                )
            }

            // Menu Section
            item {
                Column(modifier = Modifier.padding(top = 24.dp)) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .glassmorphism(shape = RoundedCornerShape(24.dp)),
                        color = Color.Transparent
                    ) {
                        Column {
                            ProfileMenuItem(
                                icon = Icons.Rounded.Edit,
                                title = stringResource(R.string.menu_edit_profile),
                                onClick = { onNavigateToEditProfile() }
                            )
                            ProfileMenuItem(
                                icon = Icons.Rounded.Bookmark,
                                title = stringResource(R.string.menu_saved_events),
                                onClick = { /* Navigate to Saved Events */ }
                            )
                            
                            // Role-based actions
                            if (user?.role == UserRole.USER) {
                                ProfileMenuItem(
                                    icon = Icons.Rounded.VerifiedUser,
                                    title = stringResource(R.string.menu_become_organizer),
                                    onClick = onNavigateToOrganizerRegister
                                )
                            } else if (user?.role == UserRole.ORGANIZER && !user.isTrusted) {
                                val statusText = when (user.trustedApplicationStatus) {
                                    ApplicationStatus.PENDING -> stringResource(R.string.menu_verification_in_progress)
                                    ApplicationStatus.REJECTED -> stringResource(R.string.btn_retry)
                                    else -> null
                                }
                                
                                ProfileMenuItem(
                                    icon = Icons.Rounded.VerifiedUser,
                                    title = stringResource(R.string.menu_apply_trusted),
                                    trailingText = statusText,
                                    onClick = {
                                        if (user.trustedApplicationStatus != ApplicationStatus.PENDING) {
                                            onNavigateToTrustedApply()
                                        }
                                    }
                                )
                            }

                            ProfileMenuItem(
                                icon = Icons.Rounded.Palette,
                                title = stringResource(R.string.menu_app_theme),
                                trailingText = when(themeMode) {
                                    ThemeMode.LIGHT -> stringResource(R.string.theme_light)
                                    ThemeMode.DARK -> stringResource(R.string.theme_dark)
                                    else -> stringResource(R.string.theme_auto)
                                },
                                showArrow = false,
                                onClick = { showThemeDialog = true }
                            )
                        }
                    }
                }
            }

            // My Communities Section
            val myCommunities = communities.filter { it.organizerId == user?.id }
            if (myCommunities.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(top = 24.dp)) {
                        Text(
                            text = stringResource(R.string.section_my_communities),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            items(myCommunities) { community ->
                                CommunityHorizontalCard(
                                    community = community,
                                    modifier = Modifier.width(200.dp)
                                ) { onNavigateToCommunityDetail(community.id) }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(R.string.dialog_logout_title)) },
            text = { Text(stringResource(R.string.dialog_logout_msg)) },
            confirmButton = {
                TextButton(onClick = { 
                    showLogoutDialog = false
                    onLogoutClick() 
                }) { Text(stringResource(R.string.btn_logout), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text(stringResource(R.string.btn_cancel)) }
            }
        )
    }

    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentMode = themeMode,
            onDismiss = { showThemeDialog = false },
            onSelect = { 
                onThemeChange(it)
                showThemeDialog = false
            }
        )
    }
}

@Composable
private fun ProfileHeader(
    user: User?,
    onAvatarClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.size(110.dp)) {
                Surface(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.Center)
                        .shadow(8.dp, CircleShape),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    AvatarImage(
                        imageUri = user?.avatarUri,
                        name = user?.name ?: "Guest",
                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                    )
                }
                Surface(
                    onClick = onAvatarClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-4).dp, y = (-4).dp)
                        .size(32.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    shadowElevation = 4.dp
                ) {
                    Icon(
                        Icons.Rounded.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.padding(6.dp),
                        tint = Color.White
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
            Text(
                text = user?.name ?: stringResource(R.string.guest),
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp)
            )
            Text(
                text = user?.email ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            if (user != null) {
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (user.role == UserRole.ORGANIZER && user.isTrusted) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Verified,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = Color.White
                                )
                                Text(
                                    text = stringResource(R.string.role_trusted_organizer),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        StatusBadge(
                            text = stringResource(user.role.resId),
                            isActive = true
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(12.dp))
            Text(
                text = user?.bio ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}

@Composable
private fun ProfileStats(
    communitiesCount: Int,
    eventsCount: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatItem(
            modifier = Modifier.weight(1f),
            label = stringResource(R.string.label_communities),
            value = communitiesCount.toString(),
            icon = Icons.Rounded.Groups,
            color = Color(0xFF6C5CE7)
        )
        StatItem(
            modifier = Modifier.weight(1f),
            label = stringResource(R.string.label_events),
            value = eventsCount.toString(),
            icon = Icons.Rounded.CalendarToday,
            color = Color(0xFF00B894)
        )
    }
}

@Composable
private fun StatItem(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Surface(
        modifier = modifier.glassmorphism(shape = RoundedCornerShape(16.dp)),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    trailingText: String? = null,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    showArrow: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = if (tint == MaterialTheme.colorScheme.onSurface) MaterialTheme.colorScheme.primary else tint
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = tint,
                modifier = Modifier.weight(1f)
            )
            if (trailingText != null) {
                Text(
                    text = trailingText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
            }
            if (showArrow) {
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun ThemeSelectionDialog(
    currentMode: ThemeMode,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.menu_app_theme)) },
        text = {
            Column {
                ThemeOption(stringResource(R.string.theme_auto), 0, currentMode == ThemeMode.AUTO, onSelect)
                ThemeOption(stringResource(R.string.theme_light), 1, currentMode == ThemeMode.LIGHT, onSelect)
                ThemeOption(stringResource(R.string.theme_dark), 2, currentMode == ThemeMode.DARK, onSelect)
            }
        },
        confirmButton = {}
    )
}

@Composable
private fun ThemeOption(label: String, value: Int, selected: Boolean, onSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(value) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = { onSelect(value) })
        Spacer(Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium)
    }
}

@ThemePreviews
@Composable
fun ProfileScreenPreview() {
    CommunityEventManagementTheme {
        ProfileContent(
            user = User(
                id = "1", 
                name = "Nabil", 
                email = "nabil@mail.com", 
                role = UserRole.USER,
                isTrusted = false
            ),
            communities = emptyList(),
            themeMode = ThemeMode.AUTO,
            onAvatarChange = {},
            onThemeChange = {},
            onLogoutClick = {},
            onNavigateToOrganizerRegister = {},
            onNavigateToTrustedApply = {},
            onNavigateToCommunityDetail = {},
            onNavigateToEditProfile = {}
        )
    }
}

