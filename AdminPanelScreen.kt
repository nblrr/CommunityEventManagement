package com.example.communityeventmanagement.features.admin

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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.data.model.TrustedApplication
import com.example.communityeventmanagement.data.model.UserProfile
import com.example.communityeventmanagement.ui.AppViewModelProvider
import com.example.communityeventmanagement.ui.theme.CommunityEventManagementTheme
import com.example.communityeventmanagement.ui.theme.ThemePreviews
import kotlinx.coroutines.launch

@Composable
fun AdminPanelScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminPanelViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    AdminPanelContent(
        searchQuery = viewModel.searchQuery,
        selectedTab = viewModel.selectedTab,
        users = viewModel.users,
        pendingApplications = viewModel.pendingApplications,
        userToToggleBlock = viewModel.userToToggleBlock,
        snackbarHostState = snackbarHostState,
        onSearchQueryChange = { viewModel.searchQuery = it },
        onTabSelect = { viewModel.selectedTab = it },
        onApproveApplication = { userId ->
            viewModel.approveApplication(userId) { message ->
                scope.launch { snackbarHostState.showSnackbar(message) }
            }
        },
        onRejectApplication = { userId ->
            viewModel.rejectApplication(userId) { message ->
                scope.launch { snackbarHostState.showSnackbar(message) }
            }
        },
        onBlockToggleClick = { viewModel.userToToggleBlock = it },
        onConfirmBlockToggle = { user ->
            viewModel.toggleBlock(user) { message ->
                scope.launch { snackbarHostState.showSnackbar(message) }
            }
            viewModel.userToToggleBlock = null
        },
        onDismissBlockDialog = { viewModel.userToToggleBlock = null },
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelContent(
    searchQuery: String,
    selectedTab: Int,
    users: List<UserProfile>,
    pendingApplications: List<TrustedApplication>,
    userToToggleBlock: UserProfile?,
    snackbarHostState: SnackbarHostState,
    onSearchQueryChange: (String) -> Unit,
    onTabSelect: (Int) -> Unit,
    onApproveApplication: (String) -> Unit,
    onRejectApplication: (String) -> Unit,
    onBlockToggleClick: (UserProfile) -> Unit,
    onConfirmBlockToggle: (UserProfile) -> Unit,
    onDismissBlockDialog: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.admin_panel), fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
            if (selectedTab != 2) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.search_hint_user_organizer)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(Modifier.height(16.dp))
            }

            SecondaryTabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { onTabSelect(0) }) {
                    Text(stringResource(R.string.tab_user), modifier = Modifier.padding(vertical = 12.dp))
                }
                Tab(selected = selectedTab == 1, onClick = { onTabSelect(1) }) {
                    Text(stringResource(R.string.tab_organizer), modifier = Modifier.padding(vertical = 12.dp))
                }
                Tab(selected = selectedTab == 2, onClick = { onTabSelect(2) }) {
                    BadgedBox(badge = { if (pendingApplications.isNotEmpty()) Badge { Text(pendingApplications.size.toString()) } }) {
                        Text(stringResource(R.string.tab_applications), modifier = Modifier.padding(vertical = 12.dp))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                if (selectedTab == 2) {
                    items(pendingApplications, key = { it.userId }) { application ->
                        TrustedApplicationCard(
                            application = application,
                            onApprove = { onApproveApplication(application.userId) },
                            onReject = { onRejectApplication(application.userId) },
                        )
                    }
                    if (pendingApplications.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text(stringResource(R.string.msg_no_pending_applications), color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                } else {
                    items(users, key = { it.id }) { user ->
                        UserManagementCard(
                            user = user,
                            onBlockToggle = { onBlockToggleClick(user) }
                        )
                    }
                    if (users.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text(stringResource(R.string.msg_no_data_found), color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }
            }
        }
    }

    userToToggleBlock?.let { user ->
        AlertDialog(
            onDismissRequest = onDismissBlockDialog,
            title = { Text(if (user.isBlocked) stringResource(R.string.dialog_unblock_user_title) else stringResource(R.string.dialog_block_user_title)) },
            text = { Text(stringResource(R.string.dialog_block_confirm_msg, if (user.isBlocked) stringResource(R.string.action_unblocking) else stringResource(R.string.action_blocking), user.name)) },
            confirmButton = {
                TextButton(
                    onClick = { onConfirmBlockToggle(user) }
                ) {
                    Text(if (user.isBlocked) stringResource(R.string.btn_unblock) else stringResource(R.string.btn_block), color = if (user.isBlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissBlockDialog) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}

@Composable
fun TrustedApplicationCard(
    application: TrustedApplication,
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
            Text(text = stringResource(R.string.label_community_prefix, application.communityName), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(text = stringResource(R.string.label_reason), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            Text(text = application.reason, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text(text = stringResource(R.string.label_experience), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            Text(text = application.experience, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onApprove, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) {
                    Text(stringResource(R.string.btn_approve))
                }
                OutlinedButton(
                    onClick = onReject, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.btn_reject))
                }
            }
        }
    }
}

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
                        Text(stringResource(R.string.label_blocked), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
            IconButton(onClick = onBlockToggle) {
                Icon(
                    if (user.isBlocked) Icons.Default.LockOpen else Icons.Default.Block,
                    contentDescription = if (user.isBlocked) stringResource(R.string.btn_unblock) else stringResource(R.string.btn_block),
                    tint = if (user.isBlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
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
            users = listOf(
                UserProfile("1", "Budi", "budi@mail.com", role = "User"),
                UserProfile("2", "Andi", "andi@mail.com", role = "Organizer", isTrusted = true)
            ),
            pendingApplications = listOf(
                TrustedApplication("3", "Susi", "Kucing Lovers", "Ingin memverifikasi komunitas.", "3 tahun mengelola forum.")
            ),
            userToToggleBlock = null,
            snackbarHostState = remember { SnackbarHostState() },
            onSearchQueryChange = {},
            onTabSelect = {},
            onApproveApplication = {},
            onRejectApplication = {},
            onBlockToggleClick = {},
            onConfirmBlockToggle = {},
            onDismissBlockDialog = {},
            onNavigateBack = {}
        )
    }
}
