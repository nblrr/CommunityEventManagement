package com.example.communityeventmanagement.ui.feature.home

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.domain.entities.UserRole
import com.example.communityeventmanagement.ui.components.CommunityHorizontalCard
import com.example.communityeventmanagement.ui.components.EmptyState
import com.example.communityeventmanagement.ui.components.EventCardItem
import com.example.communityeventmanagement.ui.components.glassmorphism
import com.example.communityeventmanagement.ui.theme.CommunityEventManagementTheme
import com.example.communityeventmanagement.ui.theme.ThemePreviews
import com.example.communityeventmanagement.util.CoverImage
import java.util.Calendar

@Composable
fun HomeScreen(
    onNavigateToCommunityList: () -> Unit,
    onNavigateToAdminPanel: () -> Unit,
    onNavigateToCommunityDetail: (Int) -> Unit,
    onNavigateToEventDetail: (Int, Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeContent(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onCategorySelect = viewModel::onCategoryChange,
        onDateFilterSelect = viewModel::onDateFilterChange,
        onNavigateToCommunityList = onNavigateToCommunityList,
        onNavigateToAdminPanel = onNavigateToAdminPanel,
        onNavigateToCommunityDetail = onNavigateToCommunityDetail,
        onNavigateToEventDetail = onNavigateToEventDetail
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    uiState: HomeUiState,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onDateFilterSelect: (Int) -> Unit,
    onNavigateToCommunityList: () -> Unit,
    onNavigateToAdminPanel: () -> Unit,
    onNavigateToCommunityDetail: (Int) -> Unit,
    onNavigateToEventDetail: (Int, Int) -> Unit,
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val dateFilterResIds = listOf(
        R.string.time_any, 
        R.string.time_today, 
        R.string.time_this_week, 
        R.string.time_this_month,
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.app_name), fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, letterSpacing = (-1).sp)
                },
                actions = {
                    if (uiState.currentUser?.role == UserRole.ADMIN) {
                        IconButton(onClick = onNavigateToAdminPanel) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = stringResource(R.string.admin_panel), tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Welcome, Section
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
                    val welcomeMessage = when(Calendar.getInstance()[Calendar.HOUR_OF_DAY]) {
                        in 0..11 -> stringResource(R.string.welcome_morning)
                        in 12..15 -> stringResource(R.string.welcome_afternoon)
                        in 16..18 -> stringResource(R.string.welcome_evening)
                        else -> stringResource(R.string.welcome_night)
                    }
                    Text(
                        text = welcomeMessage,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = uiState.currentUser?.name?.split(" ")?.first() ?: stringResource(R.string.guest),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Search Bar
            item {
                Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.search_placeholder), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.cd_clear), modifier = Modifier.size(18.dp))
                                }
                            } else {
                                IconButton(onClick = { showFilterSheet = true }) {
                                    Icon(Icons.Default.Tune, contentDescription = stringResource(R.string.cd_filter), tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        },
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            if (((uiState.searchQuery.isEmpty()) && (uiState.selectedCategory == HomeUiState.CATEGORY_ALL)) && (uiState.selectedDateFilter == R.string.time_any)) {
                // Featured Event
                val featuredEvent = uiState.recommendedEvents.firstOrNull()
                if (featuredEvent != null) {
                    item {
                        Column(modifier = Modifier.padding(top = 24.dp)) {
                            SectionHeader(
                                title = stringResource(R.string.featured_events), 
                                action = stringResource(R.string.see_all)
                            ) { }
                            Card(
                                onClick = { onNavigateToEventDetail(featuredEvent.id, featuredEvent.communityId) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(260.dp)
                                    .padding(horizontal = 24.dp, vertical = 12.dp),
                                shape = MaterialTheme.shapes.extraLarge,
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                            ) {
                                Box {
                                    CoverImage(imageUri = featuredEvent.coverImageUri, modifier = Modifier.fillMaxSize())
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(Color.Transparent, Color.Black),
                                                    startY = 300f
                                                )
                                            )
                                    )
                                    Column(modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)) {
                                        Surface(
                                            modifier = Modifier.glassmorphism(shape = MaterialTheme.shapes.extraSmall, alpha = 0.3f),
                                            color = Color.Transparent
                                        ) {
                                            Text(stringResource(R.string.label_featured), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Black)
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        Text(featuredEvent.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = Color.White)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text(featuredEvent.location, style = MaterialTheme.typography.bodySmall, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Popular Communities
                if (uiState.recommendedCommunities.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.padding(top = 24.dp)) {
                            SectionHeader(
                                title = stringResource(R.string.popular_communities), 
                                action = stringResource(R.string.see_all), 
                                onActionClick = onNavigateToCommunityList
                            )
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                items(uiState.recommendedCommunities, key = { it.id }) { community ->
                                    CommunityHorizontalCard(community) { onNavigateToCommunityDetail(community.id) }
                                }
                            }
                        }
                    }
                }

                // Recommended Events
                val recEvents = uiState.recommendedEvents.asSequence().drop(1).take(5).toList()
                if (recEvents.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.padding(top = 24.dp)) {
                            SectionHeader(title = stringResource(R.string.choices_for_you), action = "") {}
                        }
                    }
                    items(recEvents, key = { it.id }) { event ->
                        Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)) {
                            EventCardItem(
                                event = event,
                                isRegistered = uiState.registeredEventIds.contains(event.id),
                                onClick = { onNavigateToEventDetail(event.id, event.communityId) }
                            )
                        }
                    }
                } else if (featuredEvent == null) {
                    item {
                        EmptyState(
                            title = stringResource(R.string.no_events),
                            subtitle = stringResource(R.string.no_events_subtitle),
                            modifier = Modifier.padding(top = 32.dp)
                        )
                    }
                }
            } else {
                // Search Results
                item {
                    Text(
                        text = stringResource(R.string.search_results),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                    )
                }
                if (uiState.filteredEvents.isEmpty()) {
                    item {
                        EmptyState(
                            title = stringResource(R.string.no_search_results),
                            modifier = Modifier.padding(vertical = 32.dp),
                            icon = Icons.Default.SearchOff,
                            subtitle = stringResource(R.string.no_search_results_subtitle)
                        )
                    }
                } else {
                    items(uiState.filteredEvents, key = { it.id }) { event ->
                        Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)) {
                            EventCardItem(
                                event = event,
                                isRegistered = uiState.registeredEventIds.contains(event.id),
                                onClick = { onNavigateToEventDetail(event.id, event.communityId) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Filter Bottom Sheet
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            shape = MaterialTheme.shapes.extraLarge,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(stringResource(R.string.filter_event), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(24.dp))
                
                Text(stringResource(R.string.category), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.categories) { category ->
                        FilterChip(
                            selected = uiState.selectedCategory == category.id,
                            onClick = { onCategorySelect(category.id) },
                            label = { Text(stringResource(category.resId)) },
                            shape = MaterialTheme.shapes.small
                        )
                    }
                }
                
                Spacer(Modifier.height(24.dp))
                
                Text(stringResource(R.string.time), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(dateFilterResIds) { filterResId ->
                        FilterChip(
                            selected = uiState.selectedDateFilter == filterResId,
                            onClick = { onDateFilterSelect(filterResId) },
                            label = { Text(stringResource(filterResId)) },
                            shape = MaterialTheme.shapes.medium
                        )
                    }
                }
                
                Spacer(Modifier.height(32.dp))
                
                Button(
                    onClick = { showFilterSheet = false },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(stringResource(R.string.apply_filter), fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, action: String, onActionClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        if (action.isNotEmpty()) {
            TextButton(onClick = onActionClick) {
                Text(action, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@ThemePreviews
@Composable
fun HomeScreenPreview() {
    CommunityEventManagementTheme {
        HomeContent(
            uiState = HomeUiState(
                currentUser = com.example.communityeventmanagement.domain.entities.User(id = "1", name = "Budi Santoso", email = "budi@example.com"),
                recommendedEvents = listOf(
                    com.example.communityeventmanagement.domain.entities.Event(1, "Workshop Jetpack Compose", "Belajar Compose.", "2025-06-20", "10:00", "Gedung A", "TECHNOLOGY"),
                    com.example.communityeventmanagement.domain.entities.Event(2, "Meetup Flutter", "Sharing Flutter.", "2025-07-01", "13:00", "Online", "TECHNOLOGY")
                ),
                recommendedCommunities = listOf(
                    com.example.communityeventmanagement.domain.entities.Community(1, "Android Dev", "Komunitas Android.", "TECHNOLOGY", null, "1", "Admin")
                ),
                categories = listOf(
                    com.example.communityeventmanagement.domain.entities.Category(HomeUiState.CATEGORY_ALL, R.string.category_all),
                    com.example.communityeventmanagement.domain.entities.Category("TECHNOLOGY", R.string.cat_technology),
                    com.example.communityeventmanagement.domain.entities.Category("HOBBIES", R.string.cat_hobbies)
                )
            ),
            onSearchQueryChange = {},
            onCategorySelect = {},
            onDateFilterSelect = {},
            onNavigateToCommunityList = {},
            onNavigateToAdminPanel = {},
            onNavigateToCommunityDetail = {},
            onNavigateToEventDetail = { _, _ -> }
        )
    }
}
