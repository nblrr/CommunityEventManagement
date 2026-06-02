package com.example.communityeventmanagement.ui.feature.admin

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.domain.model.TrustedApplication
import com.example.communityeventmanagement.domain.model.User
import com.example.communityeventmanagement.domain.model.UserRole
import com.example.communityeventmanagement.ui.theme.CommunityEventManagementTheme
import com.example.communityeventmanagement.ui.theme.ThemePreviews

@Composable
fun AdminPanelScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminPanelViewModel = hiltViewModel()
) {
    val users by viewModel.users.collectAsStateWithLifecycle()
    val pendingApplications by viewModel.pendingApplications.collectAsStateWithLifecycle()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val userMessage = viewModel.userMessage

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    AdminPanelContent(
        searchQuery = viewModel.searchQuery,
        selectedTab = viewModel.selectedTab,
        users = users,
        pendingApplications = pendingApplications,
        userToToggleBlock = viewModel.userToToggleBlock,
        snackbarHostState = snackbarHostState,
        showAddAdminDialog = viewModel.showAddAdminDialog,
        newAdminName = viewModel.newAdminName,
        newAdminEmail = viewModel.newAdminEmail,
        newAdminPassword = viewModel.newAdminPassword,
        isAddingAdmin = viewModel.isAddingAdmin,
        onSearchQueryChanged = { viewModel.searchQuery = it },
        onTabSelected = { viewModel.selectedTab = it },
        onApproveApplication = viewModel::approveApplication,
        onRejectApplication = viewModel::rejectApplication,
        onUserToToggleBlockChanged = { viewModel.userToToggleBlock = it },
        onConfirmToggleBlock = { user -> 
            viewModel.toggleUserBlock(user.id)
        },
        onNavigateBack = onNavigateBack,
        onShowAddAdminDialogChanged = { viewModel.showAddAdminDialog = it },
        onNewAdminNameChanged = { viewModel.newAdminName = it },
        onNewAdminEmailChanged = { viewModel.newAdminEmail = it },
        onNewAdminPasswordChanged = { viewModel.newAdminPassword = it },
        onAddAdmin = viewModel::onAddAdmin
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelContent(
    searchQuery: String,
    selectedTab: Int,
    users: List<User>,
    pendingApplications: List<TrustedApplication>,
    userToToggleBlock: User?,
    snackbarHostState: SnackbarHostState,
    showAddAdminDialog: Boolean,
    newAdminName: String,
    newAdminEmail: String,
    newAdminPassword: String,
    isAddingAdmin: Boolean,
    onSearchQueryChanged: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onApproveApplication: (String) -> Unit,
    onRejectApplication: (String) -> Unit,
    onUserToToggleBlockChanged: (User?) -> Unit,
    onConfirmToggleBlock: (User) -> Unit,
    onNavigateBack: () -> Unit,
    onShowAddAdminDialogChanged: (Boolean) -> Unit,
    onNewAdminNameChanged: (String) -> Unit,
    onNewAdminEmailChanged: (String) -> Unit,
    onNewAdminPasswordChanged: (String) -> Unit,
    onAddAdmin: () -> Unit
) {
    val tabs = listOf(
        stringResource(R.string.tab_user),
        stringResource(R.string.tab_applications)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.admin_panel), fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(onClick = { onShowAddAdminDialogChanged(true) }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.title_add_admin))
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { onTabSelected(index) },
                        text = { Text(title) }
                    )
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text(stringResource(R.string.search_hint_user_organizer)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = MaterialTheme.shapes.medium
            )

            if (selectedTab == 0) {
                UserList(
                    users = users.filter { it.name.contains(searchQuery, ignoreCase = true) || it.email.contains(searchQuery, ignoreCase = true) },
                    onToggleBlock = onUserToToggleBlockChanged
                )
            } else {
                ApplicationList(
                    applications = pendingApplications,
                    onApprove = onApproveApplication,
                    onReject = onRejectApplication
                )
            }
        }
    }

    if (userToToggleBlock != null) {
        val action = if (userToToggleBlock.isBlocked) stringResource(R.string.action_unblocking) else stringResource(R.string.action_blocking)
        AlertDialog(
            onDismissRequest = { onUserToToggleBlockChanged(null) },
            title = { Text(if (userToToggleBlock.isBlocked) stringResource(R.string.dialog_unblock_user_title) else stringResource(R.string.dialog_block_user_title)) },
            text = { Text(stringResource(R.string.dialog_block_confirm_msg, action, userToToggleBlock.name)) },
            confirmButton = {
                TextButton(onClick = { onConfirmToggleBlock(userToToggleBlock) }) {
                    Text(if (userToToggleBlock.isBlocked) stringResource(R.string.btn_unblock) else stringResource(R.string.btn_block))
                }
            },
            dismissButton = {
                TextButton(onClick = { onUserToToggleBlockChanged(null) }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }

    if (showAddAdminDialog) {
        AddAdminDialog(
            name = newAdminName,
            email = newAdminEmail,
            password = newAdminPassword,
            isAdding = isAddingAdmin,
            onNameChanged = onNewAdminNameChanged,
            emailChanged = onNewAdminEmailChanged,
            passwordChanged = onNewAdminPasswordChanged,
            onDismiss = { onShowAddAdminDialogChanged(false) },
            onConfirm = onAddAdmin
        )
    }
}

@Composable
fun AddAdminDialog(
    name: String,
    email: String,
    password: String,
    isAdding: Boolean,
    onNameChanged: (String) -> Unit,
    emailChanged: (String) -> Unit,
    passwordChanged: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.title_add_admin)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChanged,
                    label = { Text(stringResource(R.string.label_full_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = emailChanged,
                    label = { Text(stringResource(R.string.label_email)) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = passwordChanged,
                    label = { Text(stringResource(R.string.label_password)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm, enabled = !isAdding) {
                if (isAdding) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.btn_add_admin))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isAdding) {
                Text(stringResource(R.string.btn_cancel))
            }
        }
    )
}

@Composable
fun UserList(users: List<User>, onToggleBlock: (User) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(users) { user ->
            UserItem(user = user, onToggleBlock = { onToggleBlock(user) })
        }
    }
}

@Composable
fun UserItem(user: User, onToggleBlock: () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                Text(user.name.take(1).uppercase(), fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(user.name, fontWeight = FontWeight.Bold)
                Text("${user.email} (${stringResource(user.role.resId)})", style = MaterialTheme.typography.bodySmall)
                if (user.isBlocked) {
                    Text(stringResource(R.string.label_blocked), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
            if (user.role != UserRole.ADMIN) {
                IconButton(onClick = onToggleBlock) {
                    Icon(
                        imageVector = if (user.isBlocked) Icons.Default.LockOpen else Icons.Default.Block,
                        contentDescription = null,
                        tint = if (user.isBlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun ApplicationList(applications: List<TrustedApplication>, onApprove: (String) -> Unit, onReject: (String) -> Unit) {
    if (applications.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.msg_no_pending_applications), color = MaterialTheme.colorScheme.outline)
        }
    } else {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(applications) { app ->
                ApplicationItem(application = app, onApprove = { onApprove(app.userId) }, onReject = { onReject(app.userId) })
            }
        }
    }
}

@Composable
fun ApplicationItem(application: TrustedApplication, onApprove: () -> Unit, onReject: () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(application.userName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.label_community_prefix, application.communityName), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.label_reason), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
            Text(application.reason, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(4.dp))
            Text(stringResource(R.string.label_experience), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
            Text(application.experience, style = MaterialTheme.typography.bodySmall)
            
            Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onReject, modifier = Modifier.weight(1f), colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Text(stringResource(R.string.btn_reject))
                }
                Button(onClick = onApprove, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.btn_approve))
                }
            }
        }
    }
}

@ThemePreviews
@Composable
fun AdminPanelScreenPreview() {
    CommunityEventManagementTheme {
        AdminPanelContent(
            searchQuery = "",
            selectedTab = 0,
            users = listOf(User("1", "Andi", "andi@mail.com")),
            pendingApplications = emptyList(),
            userToToggleBlock = null,
            snackbarHostState = remember { SnackbarHostState() },
            showAddAdminDialog = false,
            newAdminName = "",
            newAdminEmail = "",
            newAdminPassword = "",
            isAddingAdmin = false,
            onSearchQueryChanged = {},
            onTabSelected = {},
            onApproveApplication = {},
            onRejectApplication = {},
            onUserToToggleBlockChanged = {},
            onConfirmToggleBlock = {},
            onNavigateBack = {},
            onShowAddAdminDialogChanged = {},
            onNewAdminNameChanged = {},
            onNewAdminEmailChanged = {},
            onNewAdminPasswordChanged = {},
            onAddAdmin = {}
        )
    }
}

