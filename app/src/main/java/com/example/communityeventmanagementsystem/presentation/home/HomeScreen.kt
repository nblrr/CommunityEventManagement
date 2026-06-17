package com.example.communityeventmanagementsystem.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.communityeventmanagementsystem.presentation.components.ProfileAvatar
import com.example.communityeventmanagementsystem.presentation.components.SkeletonHome
import com.example.communityeventmanagementsystem.presentation.components.AppError
import com.example.communityeventmanagementsystem.domain.model.Category
import com.example.communityeventmanagementsystem.domain.model.Community
import com.example.communityeventmanagementsystem.domain.model.Event
import com.example.communityeventmanagementsystem.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCategory: (categoryId: Long) -> Unit = {},
    onNavigateToEventDetail: (eventId: Long) -> Unit = {},
    onNavigateToCommunityDetail: (communityId: Long) -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToCreateCommunity: () -> Unit = {},
    onNavigateToCreateEvent: () -> Unit = {},
    onNavigateToSearchAndFilter: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.handleEvent(HomeContract.Event.LoadHomeData)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeContract.Effect.NavigateToLogin -> onNavigateToLogin()
                else -> {}
            }
        }
    }

    val hasAnyData = state.categories.isNotEmpty() || 
                     state.upcomingEvents.isNotEmpty() || 
                     state.recommendedEvents.isNotEmpty() || 
                     state.myCommunities.isNotEmpty()

    Scaffold(
        topBar = { 
            HomeTopBar(
                userName = state.userName,
                userAvatar = state.userAvatar,
                onProfileClick = onNavigateToProfile,
                onNotificationsClick = onNavigateToNotifications
            ) 
        },
        floatingActionButton = {
            if (state.userRole == "ORGANIZER" || state.userRole == "ADMIN") {
                FloatingActionButton(
                    onClick = onNavigateToCreateEvent,
                    containerColor = Primary,
                    contentColor = OnPrimary,
                    shape = Shapes.ExtraLarge
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Event")
                }
            }
        },
        containerColor = Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading && !hasAnyData) {
                SkeletonHome()
            } else if (state.error != null && !hasAnyData) {
                AppError(
                    message = state.error ?: "Terjadi kesalahan loading data",
                    errorCode = state.errorCode,
                    onRetry = {
                        if (state.errorCode == 401) {
                            viewModel.handleEvent(HomeContract.Event.Logout)
                        } else {
                            viewModel.handleEvent(HomeContract.Event.RefreshHomeData)
                        }
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = Dimens.SpacingLg),
                    verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLg)
                ) {
                    item {
                        Column(modifier = Modifier.padding(horizontal = Dimens.ContainerPadding)) {
                            Text(
                                text = if (state.userName != null) "Hi, ${state.userName}!" else "Discover Events",
                                style = HeadlineLgMobile,
                                color = OnSurface
                            )
                            Text("Find what connects you to the community.", style = BodyLg, color = OnSurfaceVariant, modifier = Modifier.padding(bottom = Dimens.SpacingLg))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)
                            ) {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = { Text("Search events...", style = BodyMd, color = OnSurfaceVariant) },
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = OnSurfaceVariant) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onNavigateToSearchAndFilter() },
                                    enabled = false, // Tap to navigate to SearchAndFilter Screen
                                    shape = Shapes.Large,
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = SurfaceContainer,
                                        disabledContainerColor = SurfaceContainer,
                                        unfocusedBorderColor = Color.Transparent,
                                        disabledBorderColor = Color.Transparent
                                    )
                                )
                                Button(
                                    onClick = onNavigateToSearchAndFilter,
                                    modifier = Modifier.size(56.dp),
                                    shape = Shapes.Large,
                                    contentPadding = PaddingValues(0.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainer, contentColor = OnSurface)
                                ) {
                                    Icon(Icons.Default.Tune, contentDescription = "Filter")
                                }
                            }
                        }
                    }

                    // Category List
                    if (state.categories.isNotEmpty()) {
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = Dimens.ContainerPadding),
                                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)
                            ) {
                                items(state.categories) { category ->
                                    CategoryPill(category) {
                                        onNavigateToCategory(category.id)
                                    }
                                }
                            }
                        }
                    }

                    // Trending / Recommended Event
                    if (state.recommendedEvents.isNotEmpty()) {
                        item {
                            Column(modifier = Modifier.padding(horizontal = Dimens.ContainerPadding)) {
                                Text("Recommended Events", style = HeadlineMd, color = OnSurface, modifier = Modifier.padding(bottom = Dimens.SpacingMd))
                                RecommendedLargeCard(state.recommendedEvents.first()) {
                                    onNavigateToEventDetail(state.recommendedEvents.first().id)
                                }
                            }
                        }
                    }

                    // My Communities Section
                    if (state.myCommunities.isNotEmpty()) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.ContainerPadding),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("My Communities", style = HeadlineMd, color = OnSurface)
                                    Text(
                                        "Lihat Semua",
                                        style = LabelMd,
                                        color = Primary,
                                        modifier = Modifier.clickable { onNavigateToCategory(-1L) }
                                    )
                                }
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = Dimens.ContainerPadding),
                                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)
                                ) {
                                    items(state.myCommunities) { community ->
                                        JoinedCommunityItem(community) {
                                            onNavigateToCommunityDetail(community.id)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Upcoming Events Section
                    if (state.upcomingEvents.isNotEmpty()) {
                        item {
                            Column(
                                modifier = Modifier.padding(horizontal = Dimens.ContainerPadding),
                                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)
                            ) {
                                Text("Upcoming Events", style = HeadlineMd, color = OnSurface)
                                state.upcomingEvents.forEach { event ->
                                    UpcomingEventItem(event) {
                                        onNavigateToEventDetail(event.id)
                                    }
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
fun HomeTopBar(
    userName: String?,
    userAvatar: String?,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Communitix",
                style = HeadlineMd,
                color = Primary,
                fontWeight = FontWeight.Black
            )
        },
        navigationIcon = {
            ProfileAvatar(
                imageUrl = userAvatar,
                name = userName ?: "User",
                modifier = Modifier
                    .padding(start = Dimens.ContainerPadding)
                    .size(40.dp)
                    .clickable { onProfileClick() }
            )
        },
        actions = {
            IconButton(onClick = onNotificationsClick) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Primary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface.copy(alpha = 0.8f))
    )
}

@Composable
fun CategoryPill(category: Category, onClick: () -> Unit) {
    Surface(
        shape = Shapes.Full,
        color = SurfaceContainer,
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Dimens.SpacingLg, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(category.name, style = LabelMd, color = OnSurface)
        }
    }
}

@Composable
fun RecommendedLargeCard(event: Event, onClick: () -> Unit) {
    Surface(
        shape = Shapes.ExtraLarge,
        color = SurfaceContainerLowest,
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clickable { onClick() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = event.coverImageUrl ?: "https://lh3.googleusercontent.com/aida-public/AB6AXuCYxglRY4VBF9hJNgo8ECLT45DgUsQdLDQJOIpsy6ca_JacicRCExX9Y6rnw_XZnK5lSKL0rXXF2uv0QELy37At59C5932Cudphf4JhrhTbyuCPYCk76Cu34W0UKxrahcUuMuIyjOULguC45e-G8YXW4Rla0cuE7R2A4ZliDlqwDTAlFpaOmk6PzeYWUNqJ2K4XV37n8Ond7wdHqA8Mp4A2mG1Mnd7YG_2cO6oRklitke4TlUdjgKjbNx6zUbfmjXrZlbsR0W2KtfyV",
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )
            Surface(
                color = Secondary.copy(alpha = 0.9f),
                shape = Shapes.Full,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(Dimens.SpacingMd)
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = OnSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Trending", style = LabelMd, color = OnSecondary)
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(Dimens.SpacingLg)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = InverseOnSurface, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(event.eventDate, style = LabelMd, color = InverseOnSurface)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = InverseOnSurface, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(event.location, style = LabelMd, color = InverseOnSurface)
                    }
                }
                Text(event.title, style = HeadlineLgMobile, color = SurfaceContainerLowest, modifier = Modifier.padding(bottom = 8.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun JoinedCommunityItem(community: Community, onClick: () -> Unit) {
    Surface(
        shape = Shapes.ExtraLarge,
        color = SurfaceContainerLowest,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceContainerHigh),
        modifier = Modifier
            .width(280.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(Dimens.SpacingMd)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMd),
                modifier = Modifier.padding(bottom = Dimens.SpacingSm)
            ) {
                AsyncImage(
                    model = community.coverImageUrl ?: "https://lh3.googleusercontent.com/aida-public/AB6AXuCWrqSPZvMdrHSgPjLJRAmUZlx-ca9r4LRGemVxHWbIJIv8eLIKrkKWkZruOFcM7NbVtTzaKXhO6B05aDtXbhs2IebODF0HJEfza1MfvnOViHYSv6kghB1P-cdUYBIunwyJPWPCEOj2gSHZhS4VCOd0gey8yOGf5f6REwmlC6tSMB1_WBNF763AA4Tq8IlD2oiImvqF_DzV3MpUJeFEcc9k2ofaSTklto-CSNyONkbgTpCG6JVORrYIt3MPVr6CpN2RFcol4q1ZgsZz",
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(community.name, style = BodyLg.copy(fontWeight = FontWeight.Bold), color = OnSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("${community.memberCount} Anggota", style = BodySm, color = OnSurfaceVariant)
                }
            }
            Text(community.description ?: "", style = BodySm, color = OnSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun UpcomingEventItem(event: Event, onClick: () -> Unit) {
    Surface(
        shape = Shapes.ExtraLarge,
        color = SurfaceContainerLowest,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceContainer),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column {
            if (event.coverImageUrl != null) {
                AsyncImage(
                    model = event.coverImageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(128.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth().height(128.dp).background(SurfaceContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Hiking, contentDescription = null, tint = OutlineVariant, modifier = Modifier.size(48.dp))
                }
            }
            Column(modifier = Modifier.padding(Dimens.SpacingMd)) {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(event.categoryName ?: "Kategori", style = LabelMd, color = Primary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(event.eventTime, style = BodySm, color = OnSurfaceVariant)
                    }
                }
                Text(event.title, style = BodyLg.copy(fontWeight = FontWeight.SemiBold), color = OnSurface, modifier = Modifier.padding(bottom = 4.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(event.description ?: "", style = BodySm, color = OnSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(bottom = 12.dp))
                HorizontalDivider(color = SurfaceContainerHigh)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(event.location, style = BodySm, color = OnSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Surface(
                        color = Primary.copy(alpha = 0.1f),
                        shape = Shapes.Full
                    ) {
                        Text("${event.attendeeCount} Hadir", style = LabelMd, color = Primary, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                    }
                }
            }
        }
    }
}
