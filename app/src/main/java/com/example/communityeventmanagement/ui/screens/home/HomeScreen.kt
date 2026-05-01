package com.example.communityeventmanagement.ui.screens.home

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.communityeventmanagement.data.model.UserProfile
import com.example.communityeventmanagement.data.repository.AppState
import com.example.communityeventmanagement.ui.components.CommunityDashboardCard
import com.example.communityeventmanagement.ui.components.CommunityEventCard
import com.example.communityeventmanagement.ui.components.EmptyState
import com.example.communityeventmanagement.util.CoverImage
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    currentUser: UserProfile?,
    onNavigateToCommunityList: () -> Unit,
    onNavigateToAdminPanel: () -> Unit,
    onNavigateToCommunityDetail: (Int) -> Unit,
    onNavigateToEventDetail: (Int, Int) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val dateFilters = listOf("Kapan Saja", "Hari Ini", "Minggu Ini", "Bulan Ini")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Communitix", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, letterSpacing = (-1).sp)
                },
                actions = {
                    if (currentUser != null && currentUser.role == "Admin") {
                        IconButton(onClick = onNavigateToAdminPanel) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
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
                    val timeOfDay = when(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                        in 0..11 -> "Pagi"
                        in 12..15 -> "Siang"
                        in 16..18 -> "Sore"
                        else -> "Malam"
                    }
                    Text(
                        text = "Selamat $timeOfDay,",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = currentUser?.name?.split(" ")?.first() ?: "Guest",
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
                        value = viewModel.searchQuery,
                        onValueChange = { viewModel.searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Cari event favoritmu...", color = MaterialTheme.colorScheme.outline) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        trailingIcon = {
                            if (viewModel.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                                }
                            } else {
                                IconButton(onClick = { showFilterSheet = true }) {
                                    Icon(Icons.Default.Tune, contentDescription = "Filter", tint = MaterialTheme.colorScheme.primary)
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

            if (viewModel.searchQuery.isEmpty() && viewModel.selectedCategory == "Semua" && viewModel.selectedDateFilter == "Kapan Saja") {
                // Event featured
                val featuredEvent = viewModel.recommendedEvents.firstOrNull()
                if (featuredEvent != null) {
                    item {
                        Column(modifier = Modifier.padding(top = 16.dp)) {
                            SectionHeader(title = "Event Unggulan", action = "Lihat Semua", onActionClick = { })
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
                                    Box(modifier = Modifier.fillMaxSize().background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)),
                                            startY = 150f
                                        )
                                    ))
                                    Column(modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)) {
                                        Surface(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp)) {
                                            Text("FEATURED", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Black)
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
                val popComms = viewModel.recommendedCommunities
                if (popComms.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.padding(top = 24.dp)) {
                            SectionHeader(title = "Komunitas Populer", action = "Lihat Semua", onActionClick = onNavigateToCommunityList)
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                items(popComms) { community ->
                                    CommunityDashboardCard(community) { onNavigateToCommunityDetail(community.id) }
                                }
                            }
                        }
                    }
                }

                // Rekomendasi Event
                val recEvents = viewModel.recommendedEvents.drop(1).take(5)
                if (recEvents.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.padding(top = 24.dp)) {
                            SectionHeader(title = "Pilihan Untukmu", action = "", onActionClick = {})
                        }
                    }
                    items(recEvents) { event ->
                        Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)) {
                            CommunityEventCard(
                                event = event,
                                isRegistered = AppState.registeredEventIds.contains(event.id),
                                onClick = { onNavigateToEventDetail(event.id, event.communityId) }
                            )
                        }
                    }
                } else if (featuredEvent == null) {
                    item {
                        EmptyState(
                            title = "Belum ada event",
                            subtitle = "Coba cari komunitas untuk melihat event mereka.",
                            modifier = Modifier.padding(top = 32.dp)
                        )
                    }
                }
            } else {
                // Hasil Cari
                val filteredEvents = viewModel.filteredEvents
                item {
                    Text(
                        text = "Hasil Pencarian",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                    )
                }
                if (filteredEvents.isEmpty()) {
                    item {
                        EmptyState(
                            title = "Oops! Tidak ada hasil.",
                            modifier = Modifier.padding(vertical = 32.dp),
                            icon = Icons.Default.SearchOff,
                            subtitle = "Coba kata kunci lain atau filter berbeda."
                        )
                    }
                } else {
                    items(filteredEvents) { (event, communityId) ->
                        Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)) {
                            CommunityEventCard(
                                event = event,
                                isRegistered = AppState.registeredEventIds.contains(event.id),
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
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text("Filter Event", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(24.dp))
                
                Text("Kategori", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(viewModel.categories) { category ->
                        FilterChip(
                            selected = viewModel.selectedCategory == category,
                            onClick = { viewModel.selectedCategory = category },
                            label = { Text(category) }
                        )
                    }
                }
                
                Spacer(Modifier.height(24.dp))
                
                Text("Waktu", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(dateFilters) { filter ->
                        FilterChip(
                            selected = viewModel.selectedDateFilter == filter,
                            onClick = { viewModel.selectedDateFilter = filter },
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
                    Text("Terapkan Filter", fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

// Header bagian
@Composable
fun SectionHeader(title: String, action: String, onActionClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        if (action.isNotEmpty()) {
            TextButton(onClick = onActionClick) {
                Text(action, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
    }
}
