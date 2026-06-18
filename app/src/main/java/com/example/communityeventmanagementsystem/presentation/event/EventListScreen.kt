package com.example.communityeventmanagementsystem.presentation.event

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.communityeventmanagementsystem.presentation.components.SkeletonEventCard
import com.example.communityeventmanagementsystem.presentation.components.AppError
import com.example.communityeventmanagementsystem.presentation.components.AppEmptyState
import com.example.communityeventmanagementsystem.core.network.ErrorHandler
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import com.example.communityeventmanagementsystem.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToEventDetail: (Long) -> Unit = {},
    viewModel: EventListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val eventsItems = state.events.collectAsLazyPagingItems()
    var showFilterSheet by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(eventsItems.loadState.refresh) {
        if (eventsItems.loadState.refresh !is LoadState.Loading) {
            isRefreshing = false
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is EventListContract.Effect.NavigateToEventDetail -> {
                    onNavigateToEventDetail(effect.eventId)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Communitix",
                        style = HeadlineSm,
                        color = Primary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface.copy(alpha = 0.8f)
                )
            )
        },
        containerColor = Background
    ) { paddingValues ->
        val pullToRefreshState = rememberPullToRefreshState()
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                eventsItems.refresh()
            },
            state = pullToRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = Dimens.SpacingLg),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLg)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.ContainerPadding),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)
                    ) {
                        OutlinedTextField(
                            value = state.searchQuery,
                            onValueChange = { 
                                viewModel.handleEvent(EventListContract.Event.SearchEvents(it))
                            },
                            placeholder = { Text("Search events...", style = BodyMd, color = OutlineVariant) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = OutlineVariant) },
                            modifier = Modifier
                                .weight(1f)
                                .shadow(1.dp, Shapes.Large),
                            shape = Shapes.Large,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = SurfaceContainerLowest,
                                focusedContainerColor = SurfaceContainerLowest,
                                unfocusedBorderColor = OutlineVariant,
                                focusedBorderColor = Primary
                            )
                        )
                        Button(
                            onClick = { showFilterSheet = true },
                            modifier = Modifier
                                .size(56.dp)
                                .shadow(4.dp, Shapes.Large),
                            shape = Shapes.Large,
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnPrimary)
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = "Filter")
                        }
                    }
                }

                if (eventsItems.loadState.refresh is LoadState.Loading) {
                    items(5) {
                        Box(modifier = Modifier.padding(horizontal = Dimens.ContainerPadding)) {
                            SkeletonEventCard()
                        }
                    }
                } else if (eventsItems.loadState.refresh is LoadState.Error) {
                    val error = (eventsItems.loadState.refresh as LoadState.Error).error
                    val friendlyMessage = when (val res = ErrorHandler.handleException<Unit>(if (error is Exception) error else Exception(error))) {
                        is NetworkResult.Error -> res.message ?: "Gagal memuat event."
                        else -> "Gagal memuat event."
                    }
                    item {
                        AppError(
                            message = friendlyMessage,
                            onRetry = { eventsItems.retry() }
                        )
                    }
                } else if (eventsItems.itemCount == 0) {
                    item {
                        val titleText = if (state.searchQuery.isNotEmpty()) "Tidak ada hasil ditemukan" else "Belum ada event"
                        AppEmptyState(
                            title = titleText,
                            description = "Coba cari dengan kata kunci lain atau pilih kategori berbeda.",
                            icon = Icons.Default.Event,
                            modifier = Modifier.fillMaxWidth().padding(top = 48.dp)
                        )
                    }
                } else {
                    items(eventsItems.itemCount) { index ->
                        val event = eventsItems[index]
                        if (event != null) {
                            Box(modifier = Modifier.padding(horizontal = Dimens.ContainerPadding)) {
                                VerticalEventCard(
                                    title = event.title,
                                    category = event.categoryName ?: "KATEGORI",
                                    location = event.location,
                                    date = "${event.eventDate} • ${event.eventTime}",
                                    joined = "${event.attendeeCount}/${event.maxAttendees} Joined",
                                    categoryColor = PrimaryFixed,
                                    onCategoryColor = Primary,
                                    imageUrl = event.coverImageUrl ?: "https://lh3.googleusercontent.com/aida-public/AB6AXuAe0IQOaUJxhip8Otnj11hlrgGCEl2Valw5CsnwUoP7NtEyTgKTL8Blph8C-KuB_I0O6ZuXZtMkU0QeXtkd2gbK9KZgHIFFNtg-tn2aqWXsa_cew8A5W_bbdiKH5TBCswehLUpkZHHYK095qohP5SZ3-GsZ6DcLRyov22nHSzxZ4L57vEeieTdM89ptOHssu5_AhuqaukdXUxWYXh763d70ETioowrR1fX2RXs9dE7bh1DQoKBHHBM1qqcHfiBb5IvkWAKlPNXlFTUP",
                                    onClick = {
                                        viewModel.handleEvent(EventListContract.Event.OnEventClicked(event.id))
                                    }
                                )
                            }
                        }
                    }
                }

                if (eventsItems.loadState.append is LoadState.Loading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Primary)
                        }
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            containerColor = Surface
        ) {
            FilterSheetContent(
                selectedCategoryId = state.categoryId,
                selectedStatus = state.status,
                selectedSortBy = state.sortBy,
                categoriesList = state.categories,
                onApplyFilters = { categoryId, status, sortBy ->
                    viewModel.handleEvent(EventListContract.Event.LoadEvents(categoryId, status, sortBy))
                    showFilterSheet = false
                },
                onDismiss = { showFilterSheet = false }
            )
        }
    }
}

@Composable
fun VerticalEventCard(
    title: String,
    category: String,
    location: String,
    date: String,
    joined: String,
    categoryColor: Color,
    onCategoryColor: Color,
    imageUrl: String,
    onClick: () -> Unit
) {
    Surface(
        shape = Shapes.ExtraLarge,
        color = SurfaceContainerLowest,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceContainerHigh),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(Shapes.Large)
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(96.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = title,
                            style = LabelMd.copy(fontWeight = FontWeight.Bold),
                            color = OnSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                        )
                        Surface(
                            color = categoryColor,
                            shape = Shapes.Small
                        ) {
                            Text(
                                text = category,
                                style = LabelMd.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = onCategoryColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(location, style = BodySm.copy(fontSize = 12.sp), color = OnSurfaceVariant)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(date, style = LabelMd.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold), color = Secondary)
                    Text(joined, style = BodySm.copy(fontSize = 11.sp), color = OnSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun FilterSheetContent(
    selectedCategoryId: Long?,
    selectedStatus: String?,
    selectedSortBy: String?,
    categoriesList: List<com.example.communityeventmanagementsystem.domain.model.Category>,
    onApplyFilters: (Long?, String?, String?) -> Unit,
    onDismiss: () -> Unit
) {
    var tempCategoryId by remember { mutableStateOf(selectedCategoryId) }
    var tempStatus by remember { mutableStateOf(selectedStatus) }
    var tempSortBy by remember { mutableStateOf(selectedSortBy) }

    val categories = remember(categoriesList) {
        listOf("All" to null) + categoriesList.map { it.name to it.id }
    }

    val statuses = remember {
        listOf(
            "All" to null,
            "Upcoming" to "UPCOMING",
            "Ongoing" to "ONGOING",
            "Completed" to "COMPLETED"
        )
    }

    val sortOptions = remember {
        listOf(
            "Terbaru" to "terbaru",
            "Terlama" to "terlama",
            "Peserta Terbanyak" to "peserta_terbanyak"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.ContainerPadding)
            .padding(bottom = Dimens.SpacingXl)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = Dimens.SpacingLg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Filters", style = HeadlineMd, color = OnSurface)
            Text("Reset", style = LabelMd, color = Primary, modifier = Modifier.clickable { 
                tempCategoryId = null
                tempStatus = null
                tempSortBy = null
            })
        }

        FilterSection(title = "Categories") {
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories.size) { index ->
                    val (name, id) = categories[index]
                    val isSelected = (tempCategoryId == id) || (id == null && (tempCategoryId == null || tempCategoryId == -1L))
                    FilterChipCustom(name, isSelected) {
                        tempCategoryId = id
                    }
                }
            }
        }

        FilterSection(title = "Status") {
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(statuses.size) { index ->
                    val (name, statusVal) = statuses[index]
                    val isSelected = tempStatus == statusVal
                    FilterChipCustom(name, isSelected) {
                        tempStatus = statusVal
                    }
                }
            }
        }

        FilterSection(title = "Sort By") {
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(sortOptions.size) { index ->
                    val (name, sortVal) = sortOptions[index]
                    val isSelected = tempSortBy == sortVal
                    FilterChipCustom(name, isSelected) {
                        tempSortBy = sortVal
                    }
                }
            }
        }

        Button(
            onClick = { onApplyFilters(tempCategoryId, tempStatus, tempSortBy) },
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(top = Dimens.SpacingMd),
            shape = Shapes.Large,
            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnPrimary)
        ) {
            Text("Show Results", style = HeadlineMd.copy(fontSize = 18.sp))
        }
    }
}

@Composable
fun FilterSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(bottom = Dimens.SpacingLg)) {
        Text(title, style = LabelMd, color = Outline, modifier = Modifier.padding(bottom = Dimens.SpacingSm))
        content()
    }
}

@Composable
fun FilterChipCustom(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = Shapes.Full,
        color = if (isSelected) Primary else Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Primary else OutlineVariant),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            style = LabelMd,
            color = if (isSelected) OnPrimary else OnSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}
