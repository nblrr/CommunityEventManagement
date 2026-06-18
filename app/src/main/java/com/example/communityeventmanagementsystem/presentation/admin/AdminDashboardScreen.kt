package com.example.communityeventmanagementsystem.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.communityeventmanagementsystem.domain.model.TrustedApplication
import com.example.communityeventmanagementsystem.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.handleEvent(AdminContract.Event.LoadDashboard)
    }

    Scaffold(
        topBar = { AdminDashboardTopBar(onNavigateBack) },
        containerColor = Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (state.error != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Dimens.ContainerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(state.error ?: "Error occurred", style = HeadlineMd, color = Error)
                    Spacer(modifier = Modifier.height(Dimens.SpacingMd))
                    Button(
                        onClick = { viewModel.handleEvent(AdminContract.Event.LoadDashboard) },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text("Retry")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Dimens.ContainerPadding),
                    contentPadding = PaddingValues(top = Dimens.SpacingMd, bottom = Dimens.SpacingXl)
                ) {
                    item { AdminDashboardHeader() }
                    item { Spacer(modifier = Modifier.height(Dimens.SpacingXl)) }
                    item {
                        AdminDashboardStats(
                            totalUsers = state.stats?.totalUsers?.toString() ?: "0",
                            activeEvents = state.stats?.totalEvents?.toString() ?: "0",
                            pendingApps = state.stats?.pendingTrustedApplications?.toString() ?: "0"
                        )
                    }
                    item { Spacer(modifier = Modifier.height(Dimens.SpacingXl)) }
                    item {
                        AdminDashboardApplicationsSection(
                            pendingApps = state.pendingApps,
                            onApprove = { viewModel.handleEvent(AdminContract.Event.OnApproveApp(it)) },
                            onReject = { viewModel.handleEvent(AdminContract.Event.OnShowRejectDialog(it)) }
                        )
                    }
                }
            }

            if (state.showRejectDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.handleEvent(AdminContract.Event.OnDismissRejectDialog) },
                    title = { Text("Reject Application", style = TitleLg, color = OnSurface) },
                    containerColor = SurfaceContainerLowest,
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                            Text("Provide feedback notes for rejection:", style = BodyMd, color = OnSurfaceVariant)
                            OutlinedTextField(
                                value = state.rejectNotes,
                                onValueChange = { viewModel.handleEvent(AdminContract.Event.OnRejectNotesChanged(it)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = Shapes.Large,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = SurfaceBright,
                                    unfocusedContainerColor = SurfaceBright,
                                    focusedTextColor = OnSurface,
                                    unfocusedTextColor = OnSurface
                                )
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { viewModel.handleEvent(AdminContract.Event.OnConfirmReject) },
                            colors = ButtonDefaults.buttonColors(containerColor = Error),
                            shape = Shapes.Full
                        ) {
                            Text("Reject", style = LabelLg)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.handleEvent(AdminContract.Event.OnDismissRejectDialog) }) {
                            Text("Cancel", style = LabelLg, color = Primary)
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Communitix Admin",
                style = HeadlineMd,
                color = Primary
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Primary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Surface.copy(alpha = 0.8f)
        )
    )
}

@Composable
fun AdminDashboardHeader() {
    Column {
        Text(
            text = "ADMIN DASHBOARD",
            style = LabelMd,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(bottom = Dimens.SpacingXs)
        )
        Text(
            text = "Trusted Organizer Applications",
            style = HeadlineSm,
            color = OnSurface
        )
    }
}

@Composable
fun AdminDashboardStats(
    totalUsers: String,
    activeEvents: String,
    pendingApps: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)) {
        AdminDashboardStatCard(
            title = "Total Users",
            value = totalUsers,
            icon = Icons.Filled.Group,
            iconBgColor = PrimaryFixed,
            iconTintColor = Primary
        )
        AdminDashboardStatCard(
            title = "Active Events",
            value = activeEvents,
            icon = Icons.Filled.Event,
            iconBgColor = SecondaryContainer,
            iconTintColor = OnSecondaryContainer
        )
        AdminDashboardStatCard(
            title = "Pending Applications",
            value = pendingApps,
            icon = Icons.Filled.PendingActions,
            iconBgColor = Color(0xFFFFF0C2),
            iconTintColor = Color(0xFF8C6D00)
        )
    }
}

@Composable
fun AdminDashboardStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconTintColor: Color
) {
    Surface(
        shape = Shapes.ExtraLarge,
        color = SurfaceContainerLowest,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceVariant),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(Dimens.SpacingLg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTintColor
                )
            }
            Column {
                Text(
                    text = title,
                    style = BodySm,
                    color = OnSurfaceVariant
                )
                Text(
                    text = value,
                    style = HeadlineMd,
                    color = OnSurface
                )
            }
        }
    }
}

@Composable
fun AdminDashboardApplicationsSection(
    pendingApps: List<TrustedApplication>,
    onApprove: (Long) -> Unit,
    onReject: (Long) -> Unit
) {
    Surface(
        shape = Shapes.ExtraLarge,
        color = SurfaceContainerLowest,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceVariant),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            AdminDashboardTabBar(pendingApps.size)
            Column(
                modifier = Modifier.padding(Dimens.SpacingMd),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)
            ) {
                if (pendingApps.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Dimens.SpacingXl),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No pending applications found", style = BodyLg, color = OnSurfaceVariant)
                    }
                } else {
                    pendingApps.forEach { app ->
                        AdminDashboardAppCard(
                            name = app.communityName,
                            community = "Applicant ID: ${app.userId}",
                            status = app.status,
                            description = app.reason ?: "No description provided",
                            experience = app.experience ?: "No experience detail provided",
                            avatarUrl = "",
                            onApprove = { onApprove(app.id) },
                            onReject = { onReject(app.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminDashboardTabBar(pendingCount: Int) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceContainerLow)
            .border(1.dp, SurfaceVariant)
            .padding(top = Dimens.SpacingSm, start = Dimens.SpacingMd, end = Dimens.SpacingMd)
    ) {
        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = Dimens.SpacingLg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = Dimens.SpacingSm)) {
                    Icon(Icons.Filled.Pending, contentDescription = null, modifier = Modifier.size(18.dp), tint = Primary)
                    Spacer(modifier = Modifier.width(Dimens.SpacingSm))
                    Text("Pending ($pendingCount)", style = LabelMd, color = Primary)
                }
                Box(modifier = Modifier.height(2.dp).width(120.dp).background(Primary))
            }
        }
    }
}

@Composable
fun AdminDashboardAppCard(
    name: String,
    community: String,
    status: String,
    description: String,
    experience: String,
    avatarUrl: String,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Surface(
        shape = Shapes.ExtraLarge,
        color = Surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Dimens.SpacingMd),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)) {
                    AsyncImage(
                        model = avatarUrl.ifEmpty { "https://via.placeholder.com/150" },
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .border(2.dp, SurfaceContainerHigh, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = name, style = HeadlineMd.copy(fontSize = 18.sp), color = OnSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(14.dp), tint = OnSurfaceVariant)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = community, style = BodySm, color = OnSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
                Surface(
                    color = Color(0xFFFFF0C2).copy(alpha = 0.5f),
                    shape = Shapes.Large,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFF0C2))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = Dimens.SpacingSm, vertical = Dimens.SpacingXs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF8C6D00))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = status.uppercase(), style = LabelMd.copy(fontSize = 10.sp), color = Color(0xFF8C6D00))
                    }
                }
            }
            Surface(
                color = SurfaceContainerLowest,
                shape = Shapes.Large,
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Reason: \"$description\"",
                        style = BodySm,
                        color = OnSurfaceVariant,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Experience: \"$experience\"",
                        style = BodySm,
                        color = OnSurfaceVariant,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.SpacingSm),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onReject,
                    colors = ButtonDefaults.textButtonColors(contentColor = Error)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(Dimens.SpacingSm))
                    Text("Reject", style = LabelMd)
                }
                Spacer(modifier = Modifier.width(Dimens.SpacingSm))
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = Secondary, contentColor = OnSecondary),
                    shape = Shapes.Large
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(Dimens.SpacingSm))
                    Text("Approve", style = LabelMd)
                }
            }
        }
    }
}
