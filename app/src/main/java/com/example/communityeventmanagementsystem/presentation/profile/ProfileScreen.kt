package com.example.communityeventmanagementsystem.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.communityeventmanagementsystem.presentation.components.ProfileAvatar
import com.example.communityeventmanagementsystem.domain.model.User
import com.example.communityeventmanagementsystem.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToSavedEvents: () -> Unit = {},
    onNavigateToTrustedApp: () -> Unit = {},
    onNavigateToOrganizerDashboard: () -> Unit = {},
    onNavigateToAdminDashboard: () -> Unit = {},
    onLogoutSuccess: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.handleEvent(ProfileContract.Event.LoadProfile)
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ProfileContract.Effect.NavigateToLogin -> onLogoutSuccess()
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = { ProfileTopBar() },
        containerColor = Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading && state.user == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (state.error != null && state.user == null) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(Dimens.ContainerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.error ?: "Gagal memuat profil",
                            style = BodyLg,
                            color = Error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(Dimens.SpacingMd))
                        Button(
                            onClick = { viewModel.handleEvent(ProfileContract.Event.LoadProfile) },
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Text("Coba Lagi")
                        }
                    }
                }
            } else {
                state.user?.let { user ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = Dimens.SpacingLg),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            ProfileInfoSection(user)
                        }
                        item {
                            StatsGridSection(user)
                        }
                        item {
                            MenuListSection(
                                user = user,
                                onEditProfileClick = onNavigateToEditProfile,
                                onSavedEventsClick = onNavigateToSavedEvents,
                                onBecomeOrganizerClick = {
                                    viewModel.handleEvent(ProfileContract.Event.BecomeOrganizer)
                                },
                                onOrganizerDashboardClick = onNavigateToOrganizerDashboard,
                                onAdminDashboardClick = onNavigateToAdminDashboard,
                                onTrustedAppClick = onNavigateToTrustedApp,
                                onLogoutClick = { viewModel.handleEvent(ProfileContract.Event.Logout) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Profil Pengguna",
                style = HeadlineMd,
                color = Primary,
                fontWeight = FontWeight.Black
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
    )
}

@Composable
fun ProfileInfoSection(user: User) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.ContainerPadding)
    ) {
        Box(modifier = Modifier.padding(bottom = Dimens.SpacingMd)) {
            ProfileAvatar(
                imageUrl = user.avatarUrl,
                name = user.name,
                modifier = Modifier.size(96.dp),
                textStyle = HeadlineXl
            )
        }
        Text(user.name, style = HeadlineLgMobile, color = OnSurface, modifier = Modifier.padding(bottom = Dimens.SpacingXs))
        Text(user.email, style = BodyMd, color = OnSurfaceVariant, modifier = Modifier.padding(bottom = Dimens.SpacingSm))
        Surface(
            color = PrimaryContainer,
            shape = Shapes.Full
        ) {
            Text(user.role.uppercase(), style = LabelMd, color = OnPrimaryContainer, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
        }
    }
}

@Composable
fun StatsGridSection(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.ContainerPadding, vertical = Dimens.SpacingXl),
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)
    ) {
        StatBox(icon = Icons.Default.Groups, count = user.communitiesCount.toString(), label = "Komunitas", modifier = Modifier.weight(1f))
        StatBox(icon = Icons.Default.CalendarToday, count = user.eventsCount.toString(), label = "Event", modifier = Modifier.weight(1f))
    }
}

@Composable
fun StatBox(icon: androidx.compose.ui.graphics.vector.ImageVector, count: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        shape = Shapes.Large,
        color = SurfaceContainerLow,
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(Dimens.SpacingMd)
        ) {
            Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.padding(bottom = Dimens.SpacingSm))
            Text(count, style = HeadlineMd, color = OnSurface)
            Text(label, style = BodySm, color = OnSurfaceVariant, modifier = Modifier.padding(top = Dimens.SpacingXs))
        }
    }
}

@Composable
fun MenuListSection(
    user: User,
    onEditProfileClick: () -> Unit,
    onSavedEventsClick: () -> Unit,
    onBecomeOrganizerClick: () -> Unit,
    onOrganizerDashboardClick: () -> Unit,
    onAdminDashboardClick: () -> Unit,
    onTrustedAppClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.ContainerPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)
    ) {
        MenuItem(icon = Icons.Default.Edit, label = "Edit Profil", onClick = onEditProfileClick)
        MenuItem(icon = Icons.Default.Bookmark, label = "Event Tersimpan", onClick = onSavedEventsClick)
        
        if (user.role == "USER") {
            MenuItem(icon = Icons.Default.VerifiedUser, label = "Daftar Jadi Organizer", onClick = onBecomeOrganizerClick)
        }
        
        if (user.role == "ORGANIZER") {
            MenuItem(icon = Icons.Default.Dashboard, label = "Dashboard Organizer", onClick = onOrganizerDashboardClick)
            MenuItem(icon = Icons.Default.WorkspacePremium, label = "Trusted Organizer Application", onClick = onTrustedAppClick)
        }
        
        if (user.role == "ADMIN") {
            MenuItem(icon = Icons.Default.AdminPanelSettings, label = "Dashboard Admin", onClick = onAdminDashboardClick)
        }
        
        Spacer(modifier = Modifier.height(Dimens.SpacingSm))
        MenuItem(icon = Icons.AutoMirrored.Filled.Logout, label = "Logout", onClick = onLogoutClick)
    }
}

@Composable
fun MenuItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Surface(
        shape = Shapes.Large,
        color = SurfaceContainerLowest,
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.2f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(Dimens.SpacingMd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = OnSurfaceVariant)
                Spacer(modifier = Modifier.width(Dimens.SpacingMd))
                Text(label, style = BodyMd.copy(fontWeight = FontWeight.Medium), color = OnSurface)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = OutlineVariant)
        }
    }
}
