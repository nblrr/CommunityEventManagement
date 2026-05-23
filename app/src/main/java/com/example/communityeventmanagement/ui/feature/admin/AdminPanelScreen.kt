package com.example.communityeventmanagement.ui.feature.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.domain.entities.TrustedApplication
import com.example.communityeventmanagement.domain.entities.User
import com.example.communityeventmanagement.ui.AppViewModelProvider
import com.example.communityeventmanagement.ui.theme.CommunityEventManagementTheme
import com.example.communityeventmanagement.ui.theme.ThemePreviews

@Composable
fun AdminPanelScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminPanelViewModel = viewModel(factory = AppViewModelProvider.Factory)
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
        onSearchQueryChanged = { viewModel.searchQuery = it },
        onTabSelected = { viewModel.selectedTab = it },
        onApproveApplication = viewModel::approveApplication,
        onRejectApplication = viewModel::rejectApplication,
        onUserToToggleBlockChanged = { viewModel.userToToggleBlock = it },
        onConfirmToggleBlock = { user -> 
            viewModel.toggleUserBlock(user.id)
        },
        onNavigateBack = onNavigateBack
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
    onSearchQueryChanged: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onApproveApplication: (String) -> Unit,
    onRejectApplication: (String) -> Unit,
    onUserToToggleBlockChanged: (User?) -> Unit,
    onConfirmToggleBlock: (User) -> Unit,
    onNavigateBack: () -> Unit,
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                Text(user.email, style = MaterialTheme.typography.bodySmall)
                if (user.isBlocked) {
                    Text(stringResource(R.string.label_blocked), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
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
            onSearchQueryChanged = {},
            onTabSelected = {},
            onApproveApplication = {},
            onRejectApplication = {},
            onUserToToggleBlockChanged = {},
            onConfirmToggleBlock = {},
            onNavigateBack = {}
        )
    }
}
