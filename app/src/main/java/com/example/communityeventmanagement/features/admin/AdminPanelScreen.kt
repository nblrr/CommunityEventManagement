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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.data.model.UserProfile
import com.example.communityeventmanagement.ui.AppViewModelProvider
import kotlinx.coroutines.launch

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
                    BadgedBox(badge = { if (viewModel.pendingApplications.isNotEmpty()) Badge { Text(viewModel.pendingApplications.size.toString()) } }) {
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
                    items(viewModel.pendingApplications, key = { it.userId }) { application ->
                        TrustedApplicationCard(
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
                    if (viewModel.pendingApplications.isEmpty()) {
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
fun TrustedApplicationCard(
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
