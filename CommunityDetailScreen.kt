package com.example.communityeventmanagement.features.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.data.model.Community
import com.example.communityeventmanagement.data.model.Event
import com.example.communityeventmanagement.data.model.UserProfile
import com.example.communityeventmanagement.ui.AppViewModelProvider
import com.example.communityeventmanagement.ui.components.CommunityEventCard
import com.example.communityeventmanagement.ui.theme.CommunityEventManagementTheme
import com.example.communityeventmanagement.ui.theme.ThemePreviews
import com.example.communityeventmanagement.util.CoverImage

@Composable
fun CommunityDetailScreen(
    communityId: Int,
    currentUser: UserProfile?,
    onNavigateBack: () -> Unit,
    onNavigateToForum: () -> Unit,
    onNavigateToCreateEvent: () -> Unit,
    onNavigateToEventDetail: (Int) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: CommunityDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val community = viewModel.getCommunity(communityId) ?: return
    val isJoined = viewModel.isJoined(communityId)
    val isOrganizer = currentUser?.id == community.organizerId || currentUser?.role == "Admin"

    CommunityDetailContent(
        community = community,
        currentUser = currentUser,
        isJoined = isJoined,
        isOrganizer = isOrganizer,
        isEventRegistered = { viewModel.isEventRegistered(it) },
        onToggleJoin = { viewModel.toggleJoin(communityId) },
        onNavigateBack = onNavigateBack,
        onNavigateToForum = onNavigateToForum,
        onNavigateToCreateEvent = onNavigateToCreateEvent,
        onNavigateToEventDetail = onNavigateToEventDetail,
        onNavigateToLogin = onNavigateToLogin
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityDetailContent(
    community: Community,
    currentUser: UserProfile?,
    isJoined: Boolean,
    isOrganizer: Boolean,
    isEventRegistered: (Int) -> Boolean,
    onToggleJoin: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToForum: () -> Unit,
    onNavigateToCreateEvent: () -> Unit,
    onNavigateToEventDetail: (Int) -> Unit,
    onNavigateToLogin: () -> Unit,
) {
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
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            if (isOrganizer) {
                FloatingActionButton(onClick = onNavigateToCreateEvent) {
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
                Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
                    CoverImage(imageUri = community.coverImageUri, modifier = Modifier.fillMaxSize())
                    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)))))
                    Column(modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                        Surface(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp)) {
                            Text(community.category, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelMedium, color = Color.White)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(community.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Groups, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.member_count_format, community.memberCount), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.weight(1f))
                        if (currentUser == null) {
                            Button(onClick = onNavigateToLogin) { Text(stringResource(R.string.btn_join)) }
                        } else if (!isOrganizer) {
                            Button(
                                onClick = onToggleJoin,
                                colors = if (isJoined) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant) else ButtonDefaults.buttonColors()
                            ) {
                                Text(if (isJoined) stringResource(R.string.status_registered) else stringResource(R.string.btn_join))
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    Text(stringResource(R.string.section_about_community), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(8.dp))
                    Text(community.description, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 24.sp)

                    Spacer(Modifier.height(24.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(stringResource(R.string.label_organizer), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                            Text(community.organizerName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(32.dp))
                    Text(stringResource(R.string.section_upcoming_events), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                }
            }

            items(community.events) { event ->
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    CommunityEventCard(
                        event = event,
                        isRegistered = isEventRegistered(event.id),
                        onClick = { onNavigateToEventDetail(event.id) }
                    )
                }
            }

            if (community.events.isEmpty()) {
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.EventBusy, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(Modifier.height(16.dp))
                        Text(stringResource(R.string.msg_no_events_yet), color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
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
                category = "Hobi",
                organizerId = "1",
                organizerName = "Budi Santoso",
                memberIds = listOf("1", "2", "3"),
                events = listOf(
                    Event(1, "Gathering Kucing Sehat", "Acara kumpul bareng.", "2025-06-20", "10:00", "Taman Kota", "Sosial", communityId = 1)
                )
            ),
            currentUser = UserProfile("2", "Andi", "andi@mail.com"),
            isJoined = false,
            isOrganizer = false,
            isEventRegistered = { false },
            onToggleJoin = {},
            onNavigateBack = {},
            onNavigateToForum = {},
            onNavigateToCreateEvent = {},
            onNavigateToEventDetail = {},
            onNavigateToLogin = {}
        )
    }
}
