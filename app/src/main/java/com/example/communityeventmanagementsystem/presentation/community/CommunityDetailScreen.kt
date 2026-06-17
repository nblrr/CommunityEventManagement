package com.example.communityeventmanagementsystem.presentation.community

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.communityeventmanagementsystem.presentation.components.ProfileAvatar
import com.example.communityeventmanagementsystem.ui.theme.*

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.communityeventmanagementsystem.domain.model.Community

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToForum: (Long) -> Unit,
    viewModel: CommunityDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CommunityDetailContract.Effect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        topBar = { CommunityDetailTopBar(onNavigateBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (state.error != null) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(Dimens.ContainerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.error ?: "Terjadi kesalahan",
                            style = BodyLg,
                            color = Error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(Dimens.SpacingMd))
                        Button(
                            onClick = { viewModel.handleEvent(CommunityDetailContract.Event.LoadDetail(state.community?.id ?: 0L)) },
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Text("Coba Lagi")
                        }
                    }
                }
            } else {
                state.community?.let { community ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        CommunityDetailHero(community)
                        Column(
                            modifier = Modifier
                                .padding(Dimens.ContainerPadding)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLg)
                        ) {
                            CommunityDetailInfoBar(
                                community = community,
                                isJoined = state.isJoined,
                                isJoining = state.isJoining,
                                onJoinClick = { viewModel.handleEvent(CommunityDetailContract.Event.JoinCommunity) },
                                onLeaveClick = { viewModel.handleEvent(CommunityDetailContract.Event.LeaveCommunity) }
                            )
                            CommunityDetailDescription(community)
                            CommunityDetailOrganizer(community)
                            CommunityDetailForumAction(onClick = {
                                if (state.isJoined) {
                                    onNavigateToForum(community.id)
                                } else {
                                    viewModel.handleEvent(CommunityDetailContract.Event.ShowErrorMessage("Silakan gabung komunitas terlebih dahulu untuk mengakses forum."))
                                }
                            })
                            CommunityDetailEvents(community)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityDetailTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "EventHub",
                style = HeadlineMd,
                color = Primary,
                fontWeight = FontWeight.Black
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Primary)
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = OnSurfaceVariant)
            }
            ProfileAvatar(
                imageUrl = null, // Using null for now as we don't have current user data here
                name = "User",
                modifier = Modifier
                    .padding(end = Dimens.ContainerPadding)
                    .size(32.dp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface.copy(alpha = 0.8f))
    )
}

@Composable
fun CommunityDetailHero(community: Community) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(192.dp)
            .background(SurfaceContainerHigh)
    ) {
        AsyncImage(
            model = community.coverImageUrl ?: "https://lh3.googleusercontent.com/aida-public/AB6AXuAJXz4KlxTwRJps_Fz6q-uUh7-gwblIlfcWoxIcw_CKRduNeWJxVGJCSiD0gPq9MEtlgH89Xb4K0Krst-63QOCnm3HxoCAryLTUVoi2XM1slZnrVVKa-iedj6R0AUy24YW2htJYc0jAik8rPL0XrtxPZHC745H95LC08RGkYOFSBavP_1iAzwBYRVl5Ae4_OsyKnigUB5hVBaKQ9n2IZS75JsMQwhidxSD9uPWtM9zuk5LFTcsKyq8_1QJXuQ0_Orjk2czS3JCZDQpP",
            contentDescription = "Banner Komunitas",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color.Transparent, OnBackground.copy(alpha = 0.8f))))
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = Dimens.ContainerPadding, vertical = Dimens.SpacingMd)
        ) {
            Surface(
                color = PrimaryContainer,
                shape = Shapes.Small,
                modifier = Modifier.padding(bottom = Dimens.SpacingSm)
            ) {
                Text(
                    text = community.categoryName ?: "Kategori",
                    style = BodySm.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                    color = OnPrimaryContainer,
                    modifier = Modifier.padding(horizontal = Dimens.SpacingSm, vertical = 4.dp)
                )
            }
            Text(
                text = community.name,
                style = HeadlineLgMobile,
                color = Color.White
            )
        }
    }
}

@Composable
fun CommunityDetailInfoBar(
    community: Community,
    isJoined: Boolean,
    isJoining: Boolean,
    onJoinClick: () -> Unit,
    onLeaveClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
        Surface(
            shape = Shapes.ExtraLarge,
            color = SurfaceContainerLow,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.SpacingMd),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                    Icon(Icons.Default.Group, contentDescription = null, tint = Outline)
                    Text("${community.memberCount} Anggota", style = BodySm, color = OnSurfaceVariant, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { if (isJoined) onLeaveClick() else onJoinClick() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isJoined) SurfaceVariant else Primary,
                        contentColor = if (isJoined) OnSurfaceVariant else OnPrimary
                    ),
                    shape = Shapes.Full,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    enabled = !isJoining
                ) {
                    Text(if (isJoined) "Keluar" else "Gabung", style = LabelMd)
                }
            }
        }
    }
}

@Composable
fun CommunityDetailDescription(community: Community) {
    Column {
        Text("Tentang Komunitas", style = HeadlineMd, color = OnSurface, modifier = Modifier.padding(bottom = Dimens.SpacingSm))
        Text(
            text = community.description ?: "Tidak ada deskripsi untuk komunitas ini.",
            style = BodyMd,
            color = OnSurfaceVariant
        )
    }
}

@Composable
fun CommunityDetailOrganizer(community: Community) {
    Surface(
        shape = Shapes.ExtraLarge,
        color = SurfaceContainerLowest,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(Dimens.SpacingMd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)
        ) {
            ProfileAvatar(
                imageUrl = null, // Backend doesn't provide organizerImageUrl yet in Community model
                name = community.organizerName ?: "Admin",
                modifier = Modifier.size(48.dp)
            )
            Column {
                Text("Organizer", style = BodySm, color = Outline)
                Text(community.organizerName ?: "Admin Komunitas", style = BodyLg.copy(fontWeight = FontWeight.SemiBold), color = OnSurface)
            }
        }
    }
}

@Composable
fun CommunityDetailForumAction(onClick: () -> Unit) {
    Surface(
        shape = Shapes.ExtraLarge,
        color = SurfaceContainerHigh,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(Dimens.SpacingMd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryFixed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Forum, contentDescription = null, tint = OnPrimaryFixed)
                }
                Text("Forum Diskusi", style = HeadlineMd, color = OnSurface)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Outline)
        }
    }
}

@Composable
fun CommunityDetailEvents(community: Community) {
    Column {
        Text("Kegiatan Mendatang", style = HeadlineMd, color = OnSurface, modifier = Modifier.padding(bottom = Dimens.SpacingMd))
        Surface(
            shape = Shapes.ExtraLarge,
            color = SurfaceContainerLowest,
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceVariant),
            modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.SpacingSm)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(Dimens.SpacingLg),
                contentAlignment = Alignment.Center
            ) {
                Text("Tidak ada kegiatan mendatang.", style = BodyMd, color = OnSurfaceVariant)
            }
        }
    }
}