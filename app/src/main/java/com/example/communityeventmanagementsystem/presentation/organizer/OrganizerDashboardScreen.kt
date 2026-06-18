package com.example.communityeventmanagementsystem.presentation.organizer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.communityeventmanagementsystem.domain.model.Community
import com.example.communityeventmanagementsystem.domain.model.Event
import com.example.communityeventmanagementsystem.core.common.DateTimeUtils
import com.example.communityeventmanagementsystem.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerDashboardScreen(
    onNavigateToCreateCommunity: () -> Unit = {},
    onNavigateToCreateEvent: () -> Unit = {},
    onNavigateToCommunityDetail: (Long) -> Unit = {},
    onNavigateToEventDetail: (Long) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    viewModel: OrganizerViewModel = hiltViewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Komunitas Saya", "Event Saya")
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.handleEvent(OrganizerContract.Event.LoadDashboard)
    }

    Scaffold(
        topBar = { OrganizerDashboardTopBar(onNavigateBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTabIndex == 0) onNavigateToCreateCommunity() else onNavigateToCreateEvent()
                },
                containerColor = Primary,
                contentColor = OnPrimary,
                shape = Shapes.ExtraLarge
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Surface,
                contentColor = Primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, style = LabelMd, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                } else if (state.error != null) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(Dimens.ContainerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.error ?: "Gagal memuat dashboard",
                                style = BodyLg,
                                color = Error,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(Dimens.SpacingMd))
                            Button(
                                onClick = { viewModel.handleEvent(OrganizerContract.Event.LoadDashboard) },
                                colors = ButtonDefaults.buttonColors(containerColor = Primary)
                            ) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                } else {
                    if (selectedTabIndex == 0) {
                        // My Communities List
                        if (state.communities.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Belum ada komunitas yang Anda kelola.", style = BodyLg, color = OnSurfaceVariant)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(Dimens.ContainerPadding),
                                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)
                            ) {
                                items(state.communities) { community ->
                                    ManagedCommunityCard(community) {
                                        onNavigateToCommunityDetail(community.id)
                                    }
                                }
                            }
                        }
                    } else {
                        // My Events List
                        if (state.events.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Belum ada event yang Anda kelola.", style = BodyLg, color = OnSurfaceVariant)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(Dimens.ContainerPadding),
                                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)
                            ) {
                                items(state.events) { event ->
                                    ManagedEventCard(
                                        event = event,
                                        onCardClick = { onNavigateToEventDetail(event.id) },
                                        onDeleteClick = { viewModel.handleEvent(OrganizerContract.Event.OnDeleteEvent(event.id)) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerDashboardTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Organizer Dashboard",
                style = HeadlineMd,
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = OnSurface)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
    )
}

@Composable
fun ManagedCommunityCard(community: Community, onClick: () -> Unit) {
    Surface(
        shape = Shapes.ExtraLarge,
        color = SurfaceContainerLowest,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceContainerHigh),
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(Dimens.SpacingMd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)
        ) {
            AsyncImage(
                model = community.coverImageUrl ?: "https://lh3.googleusercontent.com/aida-public/AB6AXuCWrqSPZvMdrHSgPjLJRAmUZlx-ca9r4LRGemVxHWbIJIv8eLIKrkKWkZruOFcM7NbVtTzaKXhO6B05aDtXbhs2IebODF0HJEfza1MfvnOViHYSv6kghB1P-cdUYBIunwyJPWPCEOj2gSHZhS4VCOd0gey8yOGf5f6REwmlC6tSMB1_WBNF763AA4Tq8IlD2oiImvqF_DzV3MpUJeFEcc9k2ofaSTklto-CSNyONkbgTpCG6JVORrYIt3MPVr6CpN2RFcol4q1ZgsZz",
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(Shapes.Large),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(community.name, style = BodyLg.copy(fontWeight = FontWeight.Bold), color = OnSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(community.description ?: "", style = BodySm, color = OnSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${community.memberCount} Anggota", style = LabelMd.copy(fontSize = 12.sp), color = Primary)
            }
        }
    }
}

@Composable
fun ManagedEventCard(
    event: Event,
    onCardClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Surface(
        shape = Shapes.ExtraLarge,
        color = SurfaceContainerLowest,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceContainer),
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
            ) {
                AsyncImage(
                    model = event.coverImageUrl ?: "https://lh3.googleusercontent.com/aida-public/AB6AXuAR3Dcj-Q2OPloo0NJlSBB_1zGtFpGMraHJK3C4BvnFUETyoAv-vp5tQ0xhAGAgY_0NB4RA-ro__H7XpSqSVbbnjt8V3uSMCE2X8j5uQK8WMbfkOn6dosqQY8hZhfscXtioXbSVr5RE1WIEb9tgMYk9puhGgsEXwGErhoUQyN_Oz18HRfI04Q7vXZFx5vB571RVw_6YwQX-mokykP9D42HFHIoqFSGvpEXpwMZnmBVTjghc7y2JNKsPzv_vtxxLZsCd1RCOfDMDA4eD",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(Dimens.SpacingSm)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .size(36.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
            Column(modifier = Modifier.padding(Dimens.SpacingMd)) {
                Text(event.title, style = BodyLg.copy(fontWeight = FontWeight.SemiBold), color = OnSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = OnSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(DateTimeUtils.formatLocation(event.isOnline, event.location), style = BodySm, color = OnSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(DateTimeUtils.formatEventDateTime(event.eventDate, event.eventTime, event.endTime), style = LabelMd.copy(fontSize = 12.sp), color = Primary)
                    Surface(
                        color = Primary.copy(alpha = 0.1f),
                        shape = Shapes.Full
                    ) {
                        Text("${event.attendeeCount} Hadir", style = LabelMd, color = Primary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                }
            }
        }
    }
}
