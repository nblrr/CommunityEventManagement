package com.example.communityeventmanagement.features.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.data.model.UserProfile
import com.example.communityeventmanagement.ui.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminPanelViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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
            if (viewModel.selectedTab != 2) {
                OutlinedTextField(
                    value = viewModel.searchQuery,
                    onValueChange = { viewModel.searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.search_hint_user_organizer)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(Modifier.height(16.dp))
            }

            SecondaryTabRow(selectedTabIndex = viewModel.selectedTab) {
                Tab(selected = viewModel.selectedTab == 0, onClick = { viewModel.selectedTab = 0 }) {
                    Text(stringResource(R.string.tab_user), modifier = Modifier.padding(vertical = 12.dp))
                }
                Tab(selected = viewModel.selectedTab == 1, onClick = { viewModel.selectedTab = 1 }) {
                    Text(stringResource(R.string.tab_organizer), modifier = Modifier.padding(vertical = 12.dp))
                }
                Tab(selected = viewModel.selectedTab == 2, onClick = { viewModel.selectedTab = 2 }) {
                    BadgedBox(badge = { if (viewModel.pendingApps.isNotEmpty()) Badge { Text(viewModel.pendingApps.size.toString()) } }) {
                        Text(stringResource(R.string.tab_applications), modifier = Modifier.padding(vertical = 12.dp))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                if (viewModel.selectedTab == 2) {
                    items(viewModel.pendingApps, key = { it.userId }) { application ->
                        TrustedAppCard(
                            application = application,
                            onApprove = {
                                viewModel.approveApplication(application.userId) { message ->
                                    scope.launch { snackbarHostState.showSnackbar(message) }
                                }
                            },
                            onReject = {
                                viewModel.rejectApplication(application.userId) { message ->
                                    scope.launch { snackbarHostState.showSnackbar(message) }
                                }
                            },
                        )
                    }
                    if (viewModel.pendingApps.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text(stringResource(R.string.msg_no_pending_applications), color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                } else {
                    items(viewModel.users, key = { it.id }) { user ->
                        UserManagementCard(
                            user = user,
                            onBlockToggle = { viewModel.userToToggleBlock = user }
                        )
                    }
                    if (viewModel.users.isEmpty()) {
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

    viewModel.userToToggleBlock?.let { user ->
        AlertDialog(
            onDismissRequest = { viewModel.userToToggleBlock = null },
            title = { Text(if (user.isBlocked) stringResource(R.string.dialog_unblock_user_title) else stringResource(R.string.dialog_block_user_title)) },
            text = { Text(stringResource(R.string.dialog_block_confirm_msg, if (user.isBlocked) stringResource(R.string.action_unblocking) else stringResource(R.string.action_blocking), user.name)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.toggleBlock(user) { message ->
                            scope.launch { snackbarHostState.showSnackbar(message) }
                        }
                        viewModel.userToToggleBlock = null
                    }
                ) {
                    Text(if (user.isBlocked) stringResource(R.string.btn_unblock) else stringResource(R.string.btn_block), color = if (user.isBlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.userToToggleBlock = null }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}

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
