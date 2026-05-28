package com.example.communityeventmanagement.ui.feature.community

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.domain.entities.AppCategories
import com.example.communityeventmanagement.domain.entities.Community
import com.example.communityeventmanagement.domain.entities.User
import com.example.communityeventmanagement.domain.entities.UserRole
import com.example.communityeventmanagement.domain.entities.findDisplayRes
import com.example.communityeventmanagement.ui.components.EventCardItem
import com.example.communityeventmanagement.ui.components.FullScreenLoading
import com.example.communityeventmanagement.ui.theme.CommunityEventManagementTheme
import com.example.communityeventmanagement.ui.theme.ThemePreviews
import com.example.communityeventmanagement.util.CoverImage

@Composable
fun CommunityDetailScreen(
    currentUser: User?,
    onNavigateBack: () -> Unit,
    onNavigateToForum: () -> Unit,
    onNavigateToCreateEvent: () -> Unit,
    onNavigateToEventDetail: (Int) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToEditCommunity: (Int) -> Unit,
    viewModel: CommunityDetailViewModel = hiltViewModel()
) {
    val community by viewModel.community.collectAsStateWithLifecycle()
    
    if (community == null) {
        FullScreenLoading()
        return
    }
    val joinedIds by viewModel.joinedCommunityIds.collectAsStateWithLifecycle()
    val isJoined = joinedIds.contains(community!!.id)
    val isOrganizer = currentUser?.id == community!!.organizerId || currentUser?.role == UserRole.ADMIN

    CommunityDetailContent(
        community = community!!,
        currentUser = currentUser,
        isJoined = isJoined,
        isOrganizer = isOrganizer,
        isEventRegistered = { viewModel.isEventRegistered(it) },
        onToggleJoin = { viewModel.toggleJoin() },
        onNavigateBack = onNavigateBack,
        onNavigateToForum = onNavigateToForum,
        onNavigateToCreateEvent = onNavigateToCreateEvent,
        onNavigateToEventDetail = onNavigateToEventDetail,
        onNavigateToLogin = onNavigateToLogin,
        onEditCommunity = { onNavigateToEditCommunity(community!!.id) },
        onDeleteCommunity = { viewModel.deleteCommunity(onNavigateBack) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityDetailContent(
    community: Community,
    currentUser: User?,
    isJoined: Boolean,
    isOrganizer: Boolean,
    isEventRegistered: (Int) -> Boolean,
    onToggleJoin: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToForum: () -> Unit,
    onNavigateToCreateEvent: () -> Unit,
    onNavigateToEventDetail: (Int) -> Unit,
    onNavigateToLogin: () -> Unit,
    onEditCommunity: () -> Unit,
    onDeleteCommunity: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(community.name, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    if (isJoined || isOrganizer) {
                        IconButton(onClick = onNavigateToForum) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = stringResource(R.string.title_forum))
                        }
                    }
                    if (isOrganizer) {
                        var expanded by remember { mutableStateOf(false) }
                        Box {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = null)
                            }
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.action_edit)) },
                                    onClick = { 
                                        expanded = false
                                        onEditCommunity() 
                                    },
                                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.action_delete)) },
                                    onClick = { 
                                        expanded = false
                                        showDeleteDialog = true 
                                    },
                                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                                    colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.error)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            if (isOrganizer) {
                FloatingActionButton(
                    onClick = onNavigateToCreateEvent,
                    shape = MaterialTheme.shapes.large,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.cd_create_event))
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                    CoverImage(imageUri = community.coverImageUri, modifier = Modifier.fillMaxSize())
                    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black))))
                    Column(modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)) {
                        Surface(color = MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.extraSmall) {
                            val categoryName = AppCategories.findDisplayRes(community.category)?.let { stringResource(it) } ?: community.category
                            Text(categoryName, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelMedium, color = Color.White)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(community.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Groups, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.member_count_format, community.memberCount), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.weight(1f))
                        if (currentUser == null) {
                            Button(onClick = onNavigateToLogin, shape = MaterialTheme.shapes.medium) { Text(stringResource(R.string.btn_join)) }
                        } else if (!isOrganizer) {
                            Button(
                                onClick = onToggleJoin,
                                shape = MaterialTheme.shapes.medium,
                                colors = if (isJoined) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant) else ButtonDefaults.buttonColors()
                            ) {
                                Text(if (isJoined) stringResource(R.string.status_registered) else stringResource(R.string.btn_join))
                            }
                        }
                    }

                    Spacer(Modifier.height(32.dp))
                    Text(stringResource(R.string.section_about_community), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(12.dp))
                    Text(community.description, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 24.sp)

                    Spacer(Modifier.height(32.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(stringResource(R.string.label_organizer), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(community.organizerName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(40.dp))
                    Text(stringResource(R.string.section_upcoming_events), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                }
            }

            items(community.events) { event ->
                Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                    EventCardItem(
                        event = event,
                        isRegistered = isEventRegistered(event.id),
                        onClick = { onNavigateToEventDetail(event.id) }
                    )
                }
            }

            if (community.events.isEmpty()) {
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.EventBusy, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                        Spacer(Modifier.height(16.dp))
                        Text(stringResource(R.string.msg_no_events_yet), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.dialog_delete_community_title)) },
            text = { Text(stringResource(R.string.dialog_delete_community_msg)) },
            confirmButton = {
                TextButton(onClick = { 
                    showDeleteDialog = false
                    onDeleteCommunity() 
                }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Text(stringResource(R.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}

@ThemePreviews
@Composable
fun CommunityDetailScreenPreview() {
    CommunityEventManagementTheme {
        CommunityDetailContent(
            community = Community(
                id = 1,
                name = "Pecinta Kucing Indonesia",
                description = "Komunitas tempat berkumpulnya para pecinta kucing untuk berbagi tips perawatan dan mengadakan gathering rutin.",
                category = "HOBBIES",
                organizerId = "1",
                organizerName = "Budi Santoso",
                memberIds = listOf("1", "2", "3"),
                events = listOf(
                    com.example.communityeventmanagement.domain.entities.Event(1, "Gathering Kucing Sehat", "Acara kumpul bareng.", "2025-06-20", "10:00", "Taman Kota", "SOCIAL", communityId = 1)
                )
            ),
            currentUser = User("2", "Andi", "andi@mail.com"),
            isJoined = false,
            isOrganizer = false,
            isEventRegistered = { false },
            onToggleJoin = {},
            onNavigateBack = {},
            onNavigateToForum = {},
            onNavigateToCreateEvent = {},
            onNavigateToEventDetail = {},
            onNavigateToLogin = {},
            onEditCommunity = {},
            onDeleteCommunity = {}
        )
    }
}
