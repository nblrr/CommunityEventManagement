package com.example.communityeventmanagementsystem.presentation.community

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.communityeventmanagementsystem.presentation.components.SkeletonCommunityCard
import com.example.communityeventmanagementsystem.presentation.components.AppError
import com.example.communityeventmanagementsystem.presentation.components.AppEmptyState
import com.example.communityeventmanagementsystem.core.network.ErrorHandler
import com.example.communityeventmanagementsystem.core.common.NetworkResult
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import com.example.communityeventmanagementsystem.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityListScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToCommunityDetail: (Long) -> Unit = {},
    viewModel: CommunityListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val communitiesItems = state.communities.collectAsLazyPagingItems()
    var showSortSheet by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val displayCategories = remember(state.categories) {
        listOf(com.example.communityeventmanagementsystem.domain.model.Category(id = -1L, name = "Semua", icon = null)) + state.categories
    }

    LaunchedEffect(communitiesItems.loadState.refresh) {
        if (communitiesItems.loadState.refresh !is LoadState.Loading) {
            isRefreshing = false
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CommunityListContract.Effect.NavigateToCommunityDetail -> {
                    onNavigateToCommunityDetail(effect.communityId)
                }
            }
        }
    }

    Scaffold(
        topBar = { 
            TopAppBar(
                title = {
                    Text(
                        text = "Communitix",
                        style = HeadlineMd,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (state.categoryId != null && state.categoryId != -1L) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Primary)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface.copy(alpha = 0.8f))
            )
        },
        containerColor = Background
    ) { paddingValues ->
        val pullToRefreshState = rememberPullToRefreshState()
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                communitiesItems.refresh()
            },
            state = pullToRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimens.ContainerPadding),
                contentPadding = PaddingValues(top = Dimens.SpacingLg, bottom = Dimens.SpacingXl),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLg)
            ) {
                item {
                    Text("Komunitas", style = HeadlineSm, color = OnSurface)
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = state.searchQuery,
                            onValueChange = { 
                                viewModel.handleEvent(CommunityListContract.Event.SearchCommunities(it))
                            },
                            placeholder = { Text("Cari komunitas...", style = BodyMd, color = OutlineVariant) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Outline) },
                            modifier = Modifier.weight(1f),
                            shape = Shapes.Full,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    viewModel.handleEvent(CommunityListContract.Event.SearchCommunities(state.searchQuery, immediate = true))
                                    keyboardController?.hide()
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = SurfaceContainerLow,
                                focusedContainerColor = SurfaceContainerLow,
                                unfocusedBorderColor = OutlineVariant,
                                focusedBorderColor = Primary
                            )
                        )
                        Button(
                            onClick = { showSortSheet = true },
                            modifier = Modifier.size(56.dp),
                            shape = Shapes.Large,
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnPrimary)
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = "Urutkan")
                        }
                    }
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSm),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(displayCategories.size) { index ->
                            val category = displayCategories[index]
                            val isSelected = (state.categoryId == category.id) || (category.id == -1L && (state.categoryId == null))
                            Surface(
                                color = if (isSelected) Primary else SurfaceContainer,
                                shape = Shapes.Full,
                                border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant) else null,
                                modifier = Modifier.clickable { 
                                    val newCatId = if (category.id == -1L) null else category.id
                                    viewModel.handleEvent(CommunityListContract.Event.LoadCommunities(categoryId = newCatId, sortBy = state.sortBy))
                                }
                            ) {
                                Text(
                                    text = category.name,
                                    style = LabelMd,
                                    color = if (isSelected) OnPrimary else OnSurface,
                                    modifier = Modifier.padding(horizontal = Dimens.SpacingMd, vertical = Dimens.SpacingSm)
                                )
                            }
                        }
                    }
                }

                if (communitiesItems.loadState.refresh is LoadState.Loading) {
                    items(5) {
                        SkeletonCommunityCard()
                    }
                } else if (communitiesItems.loadState.refresh is LoadState.Error) {
                    val error = (communitiesItems.loadState.refresh as LoadState.Error).error
                    val friendlyMessage = when (val res = ErrorHandler.handleException<Unit>(if (error is Exception) error else Exception(error))) {
                        is NetworkResult.Error -> res.message ?: "Gagal memuat komunitas."
                        else -> "Gagal memuat komunitas."
                    }
                    item {
                        AppError(
                            message = friendlyMessage,
                            onRetry = { communitiesItems.retry() }
                        )
                    }
                } else if (communitiesItems.itemCount == 0) {
                    item {
                        val titleText = if (state.searchQuery.isNotEmpty()) "Tidak ada hasil ditemukan" else "Belum ada komunitas"
                        AppEmptyState(
                            title = titleText,
                            description = "Coba cari dengan kata kunci lain atau pilih kategori berbeda.",
                            icon = Icons.Default.Group,
                            modifier = Modifier.fillMaxWidth().padding(top = 48.dp)
                        )
                    }
                } else {
                    items(communitiesItems.itemCount) { index ->
                        val community = communitiesItems[index]
                        if (community != null) {
                            CommunityCard(
                                title = community.name,
                                description = community.description ?: "",
                                members = "${community.memberCount} Anggota",
                                events = "Lihat Event",
                                category = "KOMUNITAS",
                                imageUrl = community.coverImageUrl ?: "https://lh3.googleusercontent.com/aida-public/AB6AXuCWrqSPZvMdrHSgPjLJRAmUZlx-ca9r4LRGemVxHWbIJIv8eLIKrkKWkZruOFcM7NbVtTzaKXhO6B05aDtXbhs2IebODF0HJEfza1MfvnOViHYSv6kghB1P-cdUYBIunwyJPWPCEOj2gSHZhS4VCOd0gey8yOGf5f6REwmlC6tSMB1_WBNF763AA4Tq8IlD2oiImvqF_DzV3MpUJeFEcc9k2ofaSTklto-CSNyONkbgTpCG6JVORrYIt3MPVr6CpN2RFcol4q1ZgsZz",
                                onClick = {
                                    viewModel.handleEvent(CommunityListContract.Event.OnCommunityClicked(community.id))
                                }
                            )
                        }
                    }
                }

                if (communitiesItems.loadState.append is LoadState.Loading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Primary)
                        }
                    }
                }
            }
        }
    }

    if (showSortSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSortSheet = false },
            containerColor = Surface
        ) {
            SortSheetContent(
                selectedSortBy = state.sortBy,
                onApplySort = { sortVal ->
                    viewModel.handleEvent(CommunityListContract.Event.LoadCommunities(state.categoryId, sortVal))
                    showSortSheet = false
                },
                onDismiss = { showSortSheet = false }
            )
        }
    }
}

@Composable
fun CommunityCard(
    title: String,
    description: String,
    members: String,
    events: String,
    category: String,
    imageUrl: String,
    onClick: () -> Unit
) {
    Surface(
        shape = Shapes.ExtraLarge,
        color = SurfaceContainerLowest,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceContainerHigh),
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    color = SurfaceContainerHighest.copy(alpha = 0.9f),
                    shape = Shapes.Small,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(Dimens.SpacingSm)
                ) {
                    Text(
                        text = category.uppercase(),
                        style = LabelMd.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                        color = Primary,
                        modifier = Modifier.padding(horizontal = Dimens.SpacingSm, vertical = 4.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(Dimens.SpacingMd)) {
                Text(
                    text = title,
                    style = HeadlineMd.copy(fontSize = 18.sp, lineHeight = 24.sp),
                    color = OnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = Dimens.SpacingXs)
                )
                Text(
                    text = description,
                    style = BodySm,
                    color = OnSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = Dimens.SpacingMd)
                )
                HorizontalDivider(color = SurfaceContainerHigh, modifier = Modifier.padding(bottom = Dimens.SpacingSm))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(16.dp), tint = Outline)
                        Text(members, style = BodySm.copy(fontSize = 12.sp), color = Outline)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(16.dp), tint = Outline)
                        Text(events, style = BodySm.copy(fontSize = 12.sp), color = Outline)
                    }
                }
            }
        }
    }
}

@Composable
fun SortSheetContent(
    selectedSortBy: String?,
    onApplySort: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    var tempSortBy by remember { mutableStateOf(selectedSortBy) }

    val sortOptions = remember {
        listOf(
            "Terbaru" to "terbaru",
            "Terlama" to "terlama",
            "Member Terbanyak" to "peserta_terbanyak"
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
            Text("Urutkan", style = HeadlineMd, color = OnSurface)
            Text("Reset", style = LabelMd, color = Primary, modifier = Modifier.clickable { 
                tempSortBy = null
            })
        }

        FilterSection(title = "Kriteria") {
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
            onClick = { onApplySort(tempSortBy) },
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(top = Dimens.SpacingMd),
            shape = Shapes.Large,
            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnPrimary)
        ) {
            Text("Terapkan", style = HeadlineMd.copy(fontSize = 18.sp))
        }
    }
}

@Composable
private fun FilterSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(bottom = Dimens.SpacingLg)) {
        Text(title, style = LabelMd, color = Outline, modifier = Modifier.padding(bottom = Dimens.SpacingSm))
        content()
    }
}

@Composable
private fun FilterChipCustom(text: String, isSelected: Boolean, onClick: () -> Unit) {
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
