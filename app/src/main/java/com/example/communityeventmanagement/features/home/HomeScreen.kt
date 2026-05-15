package com.example.communityeventmanagement.features.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.data.model.Community
import com.example.communityeventmanagement.data.model.Event
import com.example.communityeventmanagement.data.model.UserProfile
import com.example.communityeventmanagement.ui.AppViewModelProvider
import com.example.communityeventmanagement.ui.components.CommunityDashboardCard
import com.example.communityeventmanagement.ui.components.CommunityEventCard
import com.example.communityeventmanagement.ui.components.EmptyState
import com.example.communityeventmanagement.ui.theme.CommunityEventManagementTheme
import com.example.communityeventmanagement.ui.theme.ThemePreviews
import com.example.communityeventmanagement.util.CoverImage
import java.util.Calendar

@Composable
fun HomeScreen(
    currentUser: UserProfile?,
    onNavigateToCommunityList: () -> Unit,
    onNavigateToAdminPanel: () -> Unit,
    onNavigateToCommunityDetail: (Int) -> Unit,
    onNavigateToEventDetail: (Int, Int) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    HomeContent(
        currentUser = currentUser,
        searchQuery = viewModel.searchQuery,
        selectedCategory = viewModel.selectedCategory,
        selectedDateFilter = viewModel.selectedDateFilter,
        categories = viewModel.categories,
        recommendedEvents = viewModel.recommendedEvents,
        recommendedCommunities = viewModel.recommendedCommunities,
        filteredEvents = viewModel.filteredEvents,
        isEventRegistered = { viewModel.isEventRegistered(it) },
        onSearchQueryChange = { viewModel.searchQuery = it },
        onCategorySelect = { viewModel.selectedCategory = it },
        onDateFilterSelect = { viewModel.selectedDateFilter = it },
        onNavigateToCommunityList = onNavigateToCommunityList,
        onNavigateToAdminPanel = onNavigateToAdminPanel,
        onNavigateToCommunityDetail = onNavigateToCommunityDetail,
        onNavigateToEventDetail = onNavigateToEventDetail
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    currentUser: UserProfile?,
    searchQuery: String,
    selectedCategory: String,
    selectedDateFilter: String,
    categories: List<String>,
    recommendedEvents: List<Event>,
    recommendedCommunities: List<Community>,
    filteredEvents: List<Pair<Event, Int>>,
    isEventRegistered: (Int) -> Boolean,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onDateFilterSelect: (String) -> Unit,
    onNavigateToCommunityList: () -> Unit,
    onNavigateToAdminPanel: () -> Unit,
    onNavigateToCommunityDetail: (Int) -> Unit,
    onNavigateToEventDetail: (Int, Int) -> Unit,
) {
    var showFilterSheet by remember { mutableStateOf(value = false) }
    val sheetState = rememberModalBottomSheetState()
    val dateFilters = listOf(
        stringResource(R.string.time_any), 
        stringResource(R.string.time_today), 
        stringResource(R.string.time_this_week), 
        stringResource(R.string.time_this_month),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.app_name), fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, letterSpacing = (-1).sp)
                },
                actions = {
                    if (currentUser?.role == "Admin") {
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
            // Salam user
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
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
                        text = currentUser?.name?.split(" ")?.first() ?: stringResource(R.string.guest),
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
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.search_placeholder), color = MaterialTheme.colorScheme.outline) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.cd_clear), modifier = Modifier.size(18.dp))
                                }
                            } else {
                                IconButton(onClick = { showFilterSheet = true }) {
                                    Icon(Icons.Default.Tune, contentDescription = stringResource(R.string.cd_filter), tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        },
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            if (((searchQuery.isEmpty()) && (selectedCategory == "Semua")) && (selectedDateFilter == "Kapan Saja")) {
                // Event featured
                val featuredEvent = recommendedEvents.firstOrNull()
                if (featuredEvent != null) {
                    item {
                        Column(modifier = Modifier.padding(top = 16.dp)) {
                            SectionHeader(
                                title = stringResource(R.string.featured_events), 
                                action = stringResource(R.string.see_all)
                            ) { }
                            Card(
                                onClick = { onNavigateToEventDetail(featuredEvent.id, featuredEvent.communityId) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                                    .padding(horizontal = 24.dp, vertical = 8.dp),
                                shape = RoundedCornerShape(32.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                            ) {
                                Box {
                                    CoverImage(imageUri = featuredEvent.coverImageUri, modifier = Modifier.fillMaxSize())
                                    Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)),
                            startY = 150f
                        )
                    )
            )
            Column(modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)) {
                                        Surface(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp)) {
                                            Text(stringResource(R.string.label_featured), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Black)
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        Text(featuredEvent.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = Color.White)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
                                            Text(featuredEvent.location, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Komunitas Populer
                if (recommendedCommunities.isNotEmpty()) {
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
                                items(recommendedCommunities, key = { it.id }) { community ->
                                    CommunityDashboardCard(community) { onNavigateToCommunityDetail(community.id) }
                                }
                            }
                        }
                    }
                }

                // Rekomendasi Event
                val recEvents = recommendedEvents.asSequence().drop(1).take(5).toList()
                if (recEvents.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.padding(top = 24.dp)) {
                            SectionHeader(title = stringResource(R.string.choices_for_you), action = "") {}
                        }
                    }
                    items(recEvents, key = { it.id }) { event ->
                        Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)) {
                            CommunityEventCard(
                                event = event,
                                isRegistered = isEventRegistered(event.id),
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
                // Hasil Cari
                item {
                    Text(
                        text = stringResource(R.string.search_results),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                    )
                }
                if (filteredEvents.isEmpty()) {
                    item {
                        EmptyState(
                            title = stringResource(R.string.no_search_results),
                            modifier = Modifier.padding(vertical = 32.dp),
                            icon = Icons.Default.SearchOff,
                            subtitle = stringResource(R.string.no_search_results_subtitle)
                        )
                    }
                } else {
                    items(filteredEvents, key = { it.first.id }) { (event, communityId) ->
                        Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)) {
                            CommunityEventCard(
                                event = event,
                                isRegistered = isEventRegistered(event.id),
                                onClick = { onNavigateToEventDetail(event.id, communityId) }
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
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { onCategorySelect(category) },
                            label = { Text(category) }
                        )
                    }
                }
                
                Spacer(Modifier.height(24.dp))
                
                Text(stringResource(R.string.time), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(dateFilters) { filter ->
                        FilterChip(
                            selected = selectedDateFilter == filter,
                            onClick = { onDateFilterSelect(filter) },
                            label = { Text(filter) }
                        )
                    }
                }
                
                Spacer(Modifier.height(32.dp))
                
                Button(
                    onClick = { showFilterSheet = false },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp)
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
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
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
            currentUser = UserProfile(id = "1", name = "Budi Santoso", email = "budi@example.com"),
            searchQuery = "",
            selectedCategory = "Semua",
            selectedDateFilter = "Kapan Saja",
            categories = listOf("Semua", "Hobi", "Teknologi", "Pendidikan"),
            recommendedEvents = listOf(
                Event(1, "Workshop Jetpack Compose", "Belajar Compose.", "2025-06-20", "10:00", "Gedung A", "Teknologi"),
                Event(2, "Meetup Flutter", "Sharing Flutter.", "2025-07-01", "13:00", "Online", "Teknologi")
            ),
            recommendedCommunities = listOf(
                Community(1, "Android Dev", "Komunitas Android.", "Teknologi", null, "1", "Admin")
            ),
            filteredEvents = emptyList(),
            isEventRegistered = { false },
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
