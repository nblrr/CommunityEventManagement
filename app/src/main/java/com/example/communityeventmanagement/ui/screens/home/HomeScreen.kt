package com.example.communityeventmanagement.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.communityeventmanagement.data.model.*
import com.example.communityeventmanagement.data.repository.AppState
import com.example.communityeventmanagement.ui.components.CommunityEventCard
import com.example.communityeventmanagement.util.CoverImage
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    currentUser: UserProfile?,
    onNavigateToLogin: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCommunityList: () -> Unit,
    onNavigateToAdminPanel: () -> Unit,
    onNavigateToCommunityDetail: (Int) -> Unit,
    onNavigateToEventDetail: (Int, Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }
    var selectedDateFilter by remember { mutableStateOf("Kapan Saja") }
    var filterByMonthOnly by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    
    val dateFilters = listOf("Kapan Saja", "Hari Ini", "Minggu Ini", "Bulan Ini")
    
    val categories = listOf("Semua") + AppState.communities.map { it.category }.distinct()
    
    val allEvents = AppState.communities.flatMap { community ->
        community.events.map { it to community.id }
    }

    val recommendedCommunities = AppState.getRecommendedCommunities()
    val recommendedEvents = AppState.getRecommendedEvents()

    val filteredEvents = allEvents.filter { (event, _) ->
        val matchesQuery = (event.title.contains(searchQuery, ignoreCase = true) || 
                            event.description.contains(searchQuery, ignoreCase = true))
        val matchesCategory = (selectedCategory == "Semua") || (event.category == selectedCategory)
        
        val matchesDate = when(selectedDateFilter) {
            "Hari Ini" -> {
                val todayStr = SimpleDateFormat("d M yyyy", Locale.getDefault()).format(Date())
                event.date.trim() == todayStr
            }
            "Minggu Ini" -> {
                val cal = Calendar.getInstance()
                val currentWeek = cal.get(Calendar.WEEK_OF_YEAR)
                val currentYear = cal.get(Calendar.YEAR)
                
                try {
                    val parts = event.date.trim().split(" ")
                    val eventCal = Calendar.getInstance().apply {
                        set(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
                    }
                    eventCal.get(Calendar.WEEK_OF_YEAR) == currentWeek && eventCal.get(Calendar.YEAR) == currentYear
                } catch (_: Exception) { false }
            }
            "Bulan Ini" -> {
                val cal = Calendar.getInstance()
                val currentMonth = cal.get(Calendar.MONTH) + 1
                val currentYear = cal.get(Calendar.YEAR)
                
                try {
                    val parts = event.date.trim().split(" ")
                    parts[1].toInt() == currentMonth && parts[2].toInt() == currentYear
                } catch (_: Exception) { false }
            }
            "Custom" -> {
                datePickerState.selectedDateMillis?.let { millis ->
                    val cal = Calendar.getInstance().apply { timeInMillis = millis }
                    val day = cal.get(Calendar.DAY_OF_MONTH)
                    val month = cal.get(Calendar.MONTH) + 1
                    val year = cal.get(Calendar.YEAR)
                    
                    try {
                        val parts = event.date.trim().split(" ")
                        val eDay = parts[0].toInt()
                        val eMonth = parts[1].toInt()
                        val eYear = parts[2].toInt()
                        
                        if (filterByMonthOnly) {
                            eMonth == month && eYear == year
                        } else {
                            eDay == day && eMonth == month && eYear == year
                        }
                    } catch (_: Exception) { false }
                } ?: true
            }
            else -> true
        }
        
        matchesQuery && matchesCategory && matchesDate
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Community Event Management Platform", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                },
                navigationIcon = {
                    if (searchQuery.isNotEmpty() || selectedCategory != "Semua" || selectedDateFilter != "Kapan Saja") {
                        IconButton(onClick = { 
                            searchQuery = ""
                            selectedCategory = "Semua"
                            selectedDateFilter = "Kapan Saja"
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                        }
                    }
                },
                actions = {
                    if (currentUser == null) {
                        FilledTonalButton(onClick = onNavigateToLogin, modifier = Modifier.padding(end = 8.dp)) {
                            Text("Masuk")
                        }
                    } else {
                        if (currentUser.role == "Admin") {
                            IconButton(onClick = onNavigateToAdminPanel) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        IconButton(onClick = onNavigateToProfile) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(currentUser.name.take(1).uppercase(), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Dashboard Header
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
                    val dateStr = SimpleDateFormat("EEEE, dd MMMM", Locale.forLanguageTag("id-ID")).format(Date())
                    Text(text = dateStr.uppercase(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text(
                        text = if (currentUser != null) "Halo, ${currentUser.name}" else "Halo, Selamat Datang",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Search Bar
            item {
                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Cari event atau komunitas...", color = MaterialTheme.colorScheme.outline) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(
                                        Icons.Default.DateRange, 
                                        contentDescription = "Filter Tanggal",
                                        tint = if (selectedDateFilter == "Custom") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                    )
                                }
                            },
                            shape = RoundedCornerShape(20.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                        )
                    }
                    
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        val currentFilters = if (selectedDateFilter == "Custom") {
                            val formattedDate = datePickerState.selectedDateMillis?.let { millis ->
                                if (filterByMonthOnly) {
                                    SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("id-ID")).format(Date(millis))
                                } else {
                                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(millis))
                                }
                            } ?: "Custom"
                            dateFilters + formattedDate
                        } else {
                            dateFilters
                        }

                        items(currentFilters) { filter ->
                            val isSelected = selectedDateFilter == filter || (selectedDateFilter == "Custom" && !dateFilters.contains(filter))
                            FilterChip(
                                selected = isSelected,
                                onClick = { 
                                    if (dateFilters.contains(filter)) {
                                        selectedDateFilter = filter 
                                    } else {
                                        showDatePicker = true
                                    }
                                },
                                label = { Text(filter) },
                                leadingIcon = if (isSelected) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    selectedLabelColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }

            if (searchQuery.isEmpty() && selectedCategory == "Semua" && selectedDateFilter == "Kapan Saja") {
                // Featured Event Section (Pick one upcoming)
                val featuredEvent = recommendedEvents.firstOrNull()
                if (featuredEvent != null) {
                    item {
                        Text(
                            "Highlight Hari Ini",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                        Card(
                            onClick = { onNavigateToEventDetail(featuredEvent.id, featuredEvent.communityId) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(horizontal = 20.dp),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Box {
                                Box(modifier = Modifier.fillMaxSize().background(
                                    Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)))
                                ))
                                Column(
                                    modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                                ) {
                                    Surface(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp)) {
                                        Text("TERBARU", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    Text(featuredEvent.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(14.dp))
                                        Text(featuredEvent.location, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                                    }
                                }
                            }
                        }
                    }
                }

                // Categories Quick Scroll
                item {
                    Text(
                        "Kategori",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(categories) { category ->
                            val isSelected = selectedCategory == category
                            Surface(
                                onClick = { selectedCategory = category },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Text(
                                    text = category,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Recommended Communities
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Komunitas Populer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        TextButton(onClick = onNavigateToCommunityList) {
                            Text("Lihat Semua")
                        }
                    }
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(recommendedCommunities) { community ->
                            CommunityDashboardCard(community) { onNavigateToCommunityDetail(community.id) }
                        }
                    }
                }

                // Recommended Events
                item {
                    Text(
                        "Pilihan Untukmu",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                }
                items(recommendedEvents.take(3)) { event ->
                    Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                        CommunityEventCard(
                            event = event,
                            isJoined = AppState.registeredEventIds.contains(event.id),
                            onClick = { onNavigateToEventDetail(event.id, event.communityId) }
                        )
                    }
                }
            } else {
                // Search Results / Filtered View
                item {
                    Text(
                        text = "Hasil Pencarian",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                }
                if (filteredEvents.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("Tidak ada event yang ditemukan.", color = MaterialTheme.colorScheme.outline)
                        }
                    }
                } else {
                    items(filteredEvents) { (event, communityId) ->
                        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                            CommunityEventCard(
                                event = event,
                                isJoined = AppState.registeredEventIds.contains(event.id),
                                onClick = { onNavigateToEventDetail(event.id, communityId) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateFilter = "Custom"
                    showDatePicker = false
                }) { Text("Pilih") }
            },
            dismissButton = {
                TextButton(onClick = {
                    selectedDateFilter = "Kapan Saja"
                    showDatePicker = false
                }) { Text("Reset") }
            }
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Filter Seluruh Bulan",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Tampilkan semua event di bulan yang dipilih",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    Switch(
                        checked = filterByMonthOnly,
                        onCheckedChange = { filterByMonthOnly = it }
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
fun CommunityDashboardCard(community: Community, onClick: () -> Unit) {
    val memberCount = community.events.flatMap { it.registeredUserIds }.distinct().size
    val organizerProfile = AppState.allUsers.find { it.id == community.organizerId || it.id == community.organizerId.replace("org_", "user_") }
    val isTrusted = organizerProfile?.isTrusted ?: false

    Card(
        onClick = onClick,
        modifier = Modifier.width(200.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    CoverImage(
                        imageUri = community.coverImageUri,
                        modifier = Modifier.fillMaxSize(),
                        placeholder = {
                            Icon(Icons.Default.Groups, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                        }
                    )
                }
                
                if (isTrusted) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = "Trusted", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(4.dp).size(16.dp))
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = community.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = community.category,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.outline)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "$memberCount anggota",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
