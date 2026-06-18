package com.example.communityeventmanagementsystem.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.communityeventmanagementsystem.domain.model.Community
import com.example.communityeventmanagementsystem.domain.model.User
import com.example.communityeventmanagementsystem.core.common.DateTimeUtils
import com.example.communityeventmanagementsystem.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel,
    usersViewModel: AdminUsersViewModel = hiltViewModel(),
    communitiesViewModel: AdminCommunitiesViewModel = hiltViewModel(),
    eventsViewModel: AdminEventsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToCommunityDetail: (Long) -> Unit = {},
    onNavigateToEventDetail: (Long) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val usersState by usersViewModel.uiState.collectAsStateWithLifecycle()
    val communitiesState by communitiesViewModel.uiState.collectAsStateWithLifecycle()
    val eventsState by eventsViewModel.uiState.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Dashboard", "Users", "Communities", "Events")

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = Unit) {
        viewModel.handleEvent(AdminContract.Event.LoadDashboard)
    }

    LaunchedEffect(key1 = selectedTab) {
        when (selectedTab) {
            0 -> viewModel.handleEvent(AdminContract.Event.LoadDashboard)
            1 -> usersViewModel.handleEvent(AdminUsersContract.Event.LoadUsers)
            2 -> communitiesViewModel.handleEvent(AdminCommunitiesContract.Event.LoadCommunities)
            3 -> eventsViewModel.handleEvent(AdminEventsContract.Event.LoadEvents)
        }
    }

    LaunchedEffect(key1 = Unit) {
        usersViewModel.effect.collect { effect ->
            when (effect) {
                is AdminUsersContract.Effect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }
    LaunchedEffect(key1 = Unit) {
        communitiesViewModel.effect.collect { effect ->
            when (effect) {
                is AdminCommunitiesContract.Effect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }
    LaunchedEffect(key1 = Unit) {
        eventsViewModel.effect.collect { effect ->
            when (effect) {
                is AdminEventsContract.Effect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        topBar = { AdminDashboardTopBar(onNavigateBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceContainerLowest,
                contentColor = Primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, style = LabelLg) },
                        icon = {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Default.Dashboard
                                    1 -> Icons.Default.People
                                    2 -> Icons.Default.Groups
                                    else -> Icons.Default.Event
                                },
                                contentDescription = null
                            )
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (selectedTab) {
                    0 -> DashboardTabContent(
                        state = state,
                        viewModel = viewModel
                    )
                    1 -> UsersTabContent(
                        state = usersState,
                        viewModel = usersViewModel
                    )
                    2 -> CommunitiesTabContent(
                        state = communitiesState,
                        viewModel = communitiesViewModel,
                        onNavigateToCommunityDetail = onNavigateToCommunityDetail
                    )
                    3 -> EventsTabContent(
                        state = eventsState,
                        viewModel = eventsViewModel,
                        onNavigateToEventDetail = onNavigateToEventDetail
                    )
                }
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
                text = "Communitix Admin Area",
                style = HeadlineSm,
                color = OnPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Primary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = SurfaceContainerLowest
        )
    )
}

@Composable
fun DashboardTabContent(
    state: AdminContract.State,
    viewModel: AdminViewModel
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
            Text(state.error, style = HeadlineMd, color = Error)
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
            contentPadding = PaddingValues(top = Dimens.SpacingMd, bottom = Dimens.SpacingXl),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLg)
        ) {
            item {
                Text(
                    text = "Platform Statistics Overview",
                    style = TitleLg,
                    color = OnSurface
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            AdminDashboardStatCard(
                                title = "Total Users",
                                value = state.stats?.totalUsers?.toString() ?: "0",
                                icon = Icons.Filled.People,
                                iconBgColor = PrimaryFixed,
                                iconTintColor = Primary
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            AdminDashboardStatCard(
                                title = "Organizers",
                                value = state.stats?.totalOrganizers?.toString() ?: "0",
                                icon = Icons.Filled.Person,
                                iconBgColor = SecondaryContainer,
                                iconTintColor = OnSecondaryContainer
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            AdminDashboardStatCard(
                                title = "Communities",
                                value = state.stats?.totalCommunities?.toString() ?: "0",
                                icon = Icons.Filled.Groups,
                                iconBgColor = PrimaryFixed,
                                iconTintColor = Primary
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            AdminDashboardStatCard(
                                title = "Active Events",
                                value = state.stats?.totalEvents?.toString() ?: "0",
                                icon = Icons.Filled.Event,
                                iconBgColor = SecondaryContainer,
                                iconTintColor = OnSecondaryContainer
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            AdminDashboardStatCard(
                                title = "Registrations",
                                value = state.stats?.totalRegistrations?.toString() ?: "0",
                                icon = Icons.Filled.ConfirmationNumber,
                                iconBgColor = PrimaryFixed,
                                iconTintColor = Primary
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            AdminDashboardStatCard(
                                title = "Trusted Organizers",
                                value = state.stats?.trustedOrganizers?.toString() ?: "0",
                                icon = Icons.Filled.Verified,
                                iconBgColor = SecondaryContainer,
                                iconTintColor = OnSecondaryContainer
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            AdminDashboardStatCard(
                                title = "Blocked Users",
                                value = state.stats?.blockedUsers?.toString() ?: "0",
                                icon = Icons.Filled.Block,
                                iconBgColor = Color(0xFFFDD8D8),
                                iconTintColor = Error
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            AdminDashboardStatCard(
                                title = "Pending Apps",
                                value = state.stats?.pendingTrustedApplications?.toString() ?: "0",
                                icon = Icons.Filled.PendingActions,
                                iconBgColor = Color(0xFFFFF0C2),
                                iconTintColor = Color(0xFF8C6D00)
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Pending Trusted Applications (${state.pendingApps.size})",
                    style = TitleLg,
                    color = OnSurface,
                    modifier = Modifier.padding(top = Dimens.SpacingMd)
                )
            }

            if (state.pendingApps.isEmpty()) {
                item {
                    Surface(
                        shape = Shapes.Large,
                        color = SurfaceContainerLowest,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Dimens.SpacingXl),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No pending trusted organizer applications", style = BodyMd, color = OnSurfaceVariant)
                        }
                    }
                }
            } else {
                items(state.pendingApps) { app ->
                    AdminDashboardAppCard(
                        name = app.communityName,
                        community = "Applicant ID: ${app.userId}",
                        status = app.status,
                        description = app.reason ?: "No explanation provided.",
                        experience = app.experience ?: "No experience detail provided.",
                        avatarUrl = "",
                        onApprove = { viewModel.handleEvent(AdminContract.Event.OnApproveApp(app.id)) },
                        onReject = { viewModel.handleEvent(AdminContract.Event.OnShowRejectDialog(app.id)) }
                    )
                    Spacer(modifier = Modifier.height(Dimens.SpacingSm))
                }
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

@Composable
fun UsersTabContent(
    state: AdminUsersContract.State,
    viewModel: AdminUsersViewModel
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimens.ContainerPadding)
        ) {
            // Search and Create Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.SpacingMd),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.handleEvent(AdminUsersContract.Event.OnSearchQueryChanged(it)) },
                    placeholder = { Text("Search users...") },
                    modifier = Modifier.weight(1f),
                    shape = Shapes.Large,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.handleEvent(AdminUsersContract.Event.OnSearchQueryChanged("")) }) {
                                Icon(Icons.Default.Clear, contentDescription = null)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceContainerLowest,
                        unfocusedContainerColor = SurfaceContainerLowest
                    ),
                    singleLine = true
                )

                Button(
                    onClick = { viewModel.handleEvent(AdminUsersContract.Event.OnShowCreateDialog(true)) },
                    shape = Shapes.Large,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    modifier = Modifier.height(56.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Create", style = LabelLg)
                }
            }

            // Role and Status filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.SpacingSm),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingXs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Role filter
                var showRoleMenu by remember { mutableStateOf(false) }
                Box {
                    AssistChip(
                        onClick = { showRoleMenu = true },
                        label = { Text("Role: ${state.selectedRole}") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
                    )
                    DropdownMenu(
                        expanded = showRoleMenu,
                        onDismissRequest = { showRoleMenu = false }
                    ) {
                        listOf("ALL", "SUPER_ADMIN", "ADMIN", "ORGANIZER", "USER").forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role) },
                                onClick = {
                                    showRoleMenu = false
                                    viewModel.handleEvent(AdminUsersContract.Event.OnRoleFilterChanged(role))
                                }
                            )
                        }
                    }
                }

                // Status filter
                var showStatusMenu by remember { mutableStateOf(false) }
                Box {
                    AssistChip(
                        onClick = { showStatusMenu = true },
                        label = { Text("Status: ${state.selectedStatus}") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
                    )
                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        listOf("ALL", "ACTIVE", "BLOCKED").forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status) },
                                onClick = {
                                    showStatusMenu = false
                                    viewModel.handleEvent(AdminUsersContract.Event.OnStatusFilterChanged(status))
                                }
                            )
                        }
                    }
                }
            }

            // Users list
            if (state.isLoading && state.users.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (state.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.error, color = Error, style = BodyMd)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = Dimens.SpacingXl),
                    verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)
                ) {
                    items(state.users) { user ->
                        UserRowCard(
                            user = user,
                            onDetailClick = { viewModel.handleEvent(AdminUsersContract.Event.OnShowUserDetail(user)) },
                            onBlockToggle = {
                                if (user.isBlocked) {
                                    viewModel.handleEvent(AdminUsersContract.Event.OnUnblockUser(user.id))
                                } else {
                                    viewModel.handleEvent(AdminUsersContract.Event.OnBlockUser(user.id))
                                }
                            },
                            onChangeRole = { viewModel.handleEvent(AdminUsersContract.Event.OnShowRoleDialog(true, user)) },
                            onRevokeTrusted = { viewModel.handleEvent(AdminUsersContract.Event.OnRevokeTrusted(user.id)) },
                            onDelete = { viewModel.handleEvent(AdminUsersContract.Event.OnDeleteUser(user.id)) }
                        )
                    }
                }
            }
        }

        // Dialogs
        if (state.showCreateDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.handleEvent(AdminUsersContract.Event.OnShowCreateDialog(false)) },
                title = { Text("Create User", style = TitleLg) },
                containerColor = SurfaceContainerLowest,
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                        OutlinedTextField(
                            value = state.createName,
                            onValueChange = { viewModel.handleEvent(AdminUsersContract.Event.OnCreateFieldChanged("name", it)) },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = state.createEmail,
                            onValueChange = { viewModel.handleEvent(AdminUsersContract.Event.OnCreateFieldChanged("email", it)) },
                            label = { Text("Email Address") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = state.createPassword,
                            onValueChange = { viewModel.handleEvent(AdminUsersContract.Event.OnCreateFieldChanged("password", it)) },
                            label = { Text("Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Select Role", style = LabelLg, modifier = Modifier.padding(top = Dimens.SpacingSm))
                        var roleExpanded by remember { mutableStateOf(false) }
                        Box {
                            OutlinedButton(
                                onClick = { roleExpanded = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(state.createRole)
                                Icon(Icons.Default.ArrowDropDown, null)
                            }
                            DropdownMenu(expanded = roleExpanded, onDismissRequest = { roleExpanded = false }) {
                                listOf("ADMIN", "ORGANIZER", "USER").forEach { r ->
                                    DropdownMenuItem(
                                        text = { Text(r) },
                                        onClick = {
                                            roleExpanded = false
                                            viewModel.handleEvent(AdminUsersContract.Event.OnCreateFieldChanged("role", r))
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.handleEvent(AdminUsersContract.Event.OnCreateUser) },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.handleEvent(AdminUsersContract.Event.OnShowCreateDialog(false)) }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (state.showRoleDialog && state.roleTargetUser != null) {
            AlertDialog(
                onDismissRequest = { viewModel.handleEvent(AdminUsersContract.Event.OnShowRoleDialog(false)) },
                title = { Text("Change Role for ${state.roleTargetUser.name}", style = TitleLg) },
                containerColor = SurfaceContainerLowest,
                text = {
                    Column {
                        Text("Select new role:", style = BodyMd, modifier = Modifier.padding(bottom = Dimens.SpacingSm))
                        var roleMenuExpanded by remember { mutableStateOf(false) }
                        Box {
                            OutlinedButton(
                                onClick = { roleMenuExpanded = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(state.roleSelectedValue)
                                Icon(Icons.Default.ArrowDropDown, null)
                            }
                            DropdownMenu(expanded = roleMenuExpanded, onDismissRequest = { roleMenuExpanded = false }) {
                                listOf("ADMIN", "ORGANIZER", "USER").forEach { r ->
                                    DropdownMenuItem(
                                        text = { Text(r) },
                                        onClick = {
                                            roleMenuExpanded = false
                                            viewModel.handleEvent(AdminUsersContract.Event.OnRoleSelectedValueChange(r))
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.handleEvent(AdminUsersContract.Event.OnConfirmRoleChange) },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.handleEvent(AdminUsersContract.Event.OnShowRoleDialog(false)) }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (state.selectedUserDetail != null) {
            val user = state.selectedUserDetail
            AlertDialog(
                onDismissRequest = { viewModel.handleEvent(AdminUsersContract.Event.OnShowUserDetail(null)) },
                title = { Text("User Detailed Profile", style = TitleLg) },
                containerColor = SurfaceContainerLowest,
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.name.take(1).uppercase(),
                                    style = HeadlineSm,
                                    color = Primary
                                )
                            }
                            Column {
                                Text(user.name, style = TitleLg, fontWeight = FontWeight.Bold)
                                Text(user.email, style = BodySm, color = OnSurfaceVariant)
                            }
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = Dimens.SpacingXs),
                            thickness = DividerDefaults.Thickness,
                            color = DividerDefaults.color
                        )
                        Text("Role: ${user.role}", style = BodyMd)
                        Text("Phone Number: ${user.phoneNumber ?: "-"}", style = BodyMd)
                        Text("Gender: ${user.gender ?: "-"}", style = BodyMd)
                        Text("Birth Date: ${user.birthDate ?: "-"}", style = BodyMd)
                        Text("Status: ${if (user.isBlocked) "Blocked" else "Active"}", style = BodyMd)
                        Text("Trusted Organizer: ${if (user.isTrusted) "Yes" else "No"}", style = BodyMd)
                        Text("Bio: \"${user.bio ?: "No bio provided"}\"", style = BodySm, color = OnSurfaceVariant)
                    }
                },
                confirmButton = {
                    Button(onClick = { viewModel.handleEvent(AdminUsersContract.Event.OnShowUserDetail(null)) }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

@Composable
fun UserRowCard(
    user: User,
    onDetailClick: () -> Unit,
    onBlockToggle: () -> Unit,
    onChangeRole: () -> Unit,
    onRevokeTrusted: () -> Unit,
    onDelete: () -> Unit
) {
    val isSuperAdmin = user.role == "SUPER_ADMIN" || user.id == 1L
    
    Surface(
        shape = Shapes.Large,
        color = SurfaceContainerLowest,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDetailClick() }
    ) {
        Column(modifier = Modifier.padding(Dimens.SpacingMd)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)
            ) {
                // Avatar Initials
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (user.isBlocked) Color(0xFFFFDAD6) else PrimaryFixed
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.name.take(1).uppercase(),
                        style = TitleMd,
                        color = if (user.isBlocked) Error else Primary
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.name,
                        style = TitleMd,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = user.email,
                        style = BodySm,
                        color = OnSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Badges
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(
                        color = when (user.role) {
                            "SUPER_ADMIN" -> Color(0xFFE0C3FC)
                            "ADMIN" -> PrimaryFixed
                            "ORGANIZER" -> SecondaryContainer
                            else -> SurfaceContainerLow
                        },
                        shape = Shapes.Full,
                        modifier = Modifier.border(1.dp, OutlineVariant, Shapes.Full)
                    ) {
                        Text(
                            text = user.role,
                            style = LabelMd.copy(fontSize = 10.sp),
                            color = when (user.role) {
                                "SUPER_ADMIN" -> Color(0xFF6B00B6)
                                "ADMIN" -> Primary
                                "ORGANIZER" -> OnSecondaryContainer
                                else -> OnSurfaceVariant
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    if (user.isBlocked) {
                        Surface(
                            color = Color(0xFFFFDAD6),
                            shape = Shapes.Full
                        ) {
                            Text(
                                text = "BLOCKED",
                                style = LabelMd.copy(fontSize = 9.sp),
                                color = Error,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                            )
                        }
                    } else if (user.isTrusted) {
                        Surface(
                            color = Color(0xFFD4EDDA),
                            shape = Shapes.Full
                        ) {
                            Text(
                                text = "TRUSTED",
                                style = LabelMd.copy(fontSize = 9.sp),
                                color = Color(0xFF155724),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }

            // Quick actions if not Super Admin
            if (!isSuperAdmin) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = Dimens.SpacingSm),
                    thickness = DividerDefaults.Thickness,
                    color = SurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (user.isTrusted) {
                        TextButton(
                            onClick = onRevokeTrusted,
                            colors = ButtonDefaults.textButtonColors(contentColor = Tertiary)
                        ) {
                            Text("Revoke Trusted", style = LabelMd)
                        }
                    }
                    TextButton(
                        onClick = onChangeRole,
                        colors = ButtonDefaults.textButtonColors(contentColor = Primary)
                    ) {
                        Text("Role", style = LabelMd)
                    }
                    TextButton(
                        onClick = onBlockToggle,
                        colors = ButtonDefaults.textButtonColors(contentColor = if (user.isBlocked) Color(0xFF155724) else Error)
                    ) {
                        Text(if (user.isBlocked) "Unblock" else "Block", style = LabelMd)
                    }
                    IconButton(
                        onClick = onDelete,
                        colors = IconButtonDefaults.iconButtonColors(contentColor = Error)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CommunitiesTabContent(
    state: AdminCommunitiesContract.State,
    viewModel: AdminCommunitiesViewModel,
    onNavigateToCommunityDetail: (Long) -> Unit
) {
    var deleteConfirmId by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.ContainerPadding)
    ) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { viewModel.handleEvent(AdminCommunitiesContract.Event.OnSearchQueryChanged(it)) },
            placeholder = { Text("Search communities...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.SpacingMd, bottom = Dimens.SpacingSm),
            shape = Shapes.Large,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceContainerLowest,
                unfocusedContainerColor = SurfaceContainerLowest
            ),
            singleLine = true
        )

        if (state.isLoading && state.communities.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else if (state.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.error, color = Error)
            }
        } else if (state.communities.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No communities found.", style = BodyMd)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = Dimens.SpacingXl),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)
            ) {
                items(state.communities) { community ->
                    CommunityRowCard(
                        community = community,
                        onViewDetail = { onNavigateToCommunityDetail(community.id) },
                        onDelete = { deleteConfirmId = community.id }
                    )
                }
            }
        }
    }

    if (deleteConfirmId != null) {
        AlertDialog(
            onDismissRequest = { deleteConfirmId = null },
            title = { Text("Delete Community", style = TitleLg) },
            containerColor = SurfaceContainerLowest,
            text = { Text("Are you sure you want to permanently delete this community? All events, members, ratings, and forum records will be deleted as well.", style = BodyMd) },
            confirmButton = {
                Button(
                    onClick = {
                        val id = deleteConfirmId
                        deleteConfirmId = null
                        if (id != null) {
                            viewModel.handleEvent(AdminCommunitiesContract.Event.OnDeleteCommunity(id))
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmId = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CommunityRowCard(
    community: Community,
    onViewDetail: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = Shapes.Large,
        color = SurfaceContainerLowest,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(Dimens.SpacingMd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)
        ) {
            AsyncImage(
                model = community.coverImageUrl ?: "https://via.placeholder.com/150",
                contentDescription = "Cover Image",
                modifier = Modifier
                    .size(64.dp)
                    .clip(Shapes.Medium),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = community.name,
                    style = TitleMd,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = community.description ?: "",
                    style = BodySm,
                    color = OnSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(12.dp), tint = OnSurfaceVariant)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Organizer: ${community.organizerName ?: "Unknown"}", style = LabelMd.copy(fontSize = 10.sp), color = OnSurfaceVariant)
                    Spacer(modifier = Modifier.width(Dimens.SpacingSm))
                    Icon(Icons.Default.Groups, null, modifier = Modifier.size(12.dp), tint = OnSurfaceVariant)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("${community.memberCount} members", style = LabelMd.copy(fontSize = 10.sp), color = OnSurfaceVariant)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = onDelete, colors = IconButtonDefaults.iconButtonColors(contentColor = Error)) {
                    Icon(Icons.Default.Delete, null)
                }
                TextButton(onClick = onViewDetail) {
                    Text("Detail", style = LabelMd)
                }
            }
        }
    }
}

@Composable
fun EventsTabContent(
    state: AdminEventsContract.State,
    viewModel: AdminEventsViewModel,
    onNavigateToEventDetail: (Long) -> Unit
) {
    var deleteConfirmId by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.ContainerPadding)
    ) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { viewModel.handleEvent(AdminEventsContract.Event.OnSearchQueryChanged(it)) },
            placeholder = { Text("Search events...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.SpacingMd, bottom = Dimens.SpacingSm),
            shape = Shapes.Large,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceContainerLowest,
                unfocusedContainerColor = SurfaceContainerLowest
            ),
            singleLine = true
        )

        if (state.isLoading && state.events.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else if (state.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.error, color = Error)
            }
        } else if (state.events.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No events found.", style = BodyMd)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = Dimens.SpacingXl),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)
            ) {
                items(state.events) { event: com.example.communityeventmanagementsystem.domain.model.Event ->
                    EventRowCard(
                        event = event,
                        onViewDetail = { onNavigateToEventDetail(event.id) },
                        onDelete = { deleteConfirmId = event.id }
                    )
                }
            }
        }
    }

    if (deleteConfirmId != null) {
        AlertDialog(
            onDismissRequest = { deleteConfirmId = null },
            title = { Text("Delete Event", style = TitleLg) },
            containerColor = SurfaceContainerLowest,
            text = { Text("Are you sure you want to permanently delete this event? This will erase all user registrations, attendances, and event ratings associated with it.", style = BodyMd) },
            confirmButton = {
                Button(
                    onClick = {
                        val id = deleteConfirmId
                        deleteConfirmId = null
                        if (id != null) {
                            viewModel.handleEvent(AdminEventsContract.Event.OnDeleteEvent(id))
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmId = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun EventRowCard(
    event: com.example.communityeventmanagementsystem.domain.model.Event,
    onViewDetail: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = Shapes.Large,
        color = SurfaceContainerLowest,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(Dimens.SpacingMd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)
        ) {
            AsyncImage(
                model = event.coverImageUrl ?: "https://via.placeholder.com/150",
                contentDescription = "Event Image",
                modifier = Modifier
                    .size(64.dp)
                    .clip(Shapes.Medium),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = TitleMd,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = event.description ?: "",
                    style = BodySm,
                    color = OnSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(12.dp), tint = OnSurfaceVariant)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(DateTimeUtils.formatEventDateTime(event.eventDate, event.eventTime, event.endTime), style = LabelMd.copy(fontSize = 10.sp), color = OnSurfaceVariant)
                    Spacer(modifier = Modifier.width(Dimens.SpacingSm))
                    Icon(Icons.Default.ConfirmationNumber, null, modifier = Modifier.size(12.dp), tint = OnSurfaceVariant)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("${event.attendeeCount}/${event.maxAttendees}", style = LabelMd.copy(fontSize = 10.sp), color = OnSurfaceVariant)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = onDelete, colors = IconButtonDefaults.iconButtonColors(contentColor = Error)) {
                    Icon(Icons.Default.Delete, null)
                }
                TextButton(onClick = onViewDetail) {
                    Text("Detail", style = LabelMd)
                }
            }
        }
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
