package com.example.communityeventmanagementsystem.presentation.forum

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import com.example.communityeventmanagementsystem.domain.model.ForumMessage
import com.example.communityeventmanagementsystem.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumScreen(
    onNavigateBack: () -> Unit,
    viewModel: ForumViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(state.isLoading) {
        if (!state.isLoading) isRefreshing = false
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ForumContract.Effect.ScrollToBottom -> {
                    if (state.messages.isNotEmpty()) {
                        listState.animateScrollToItem(state.messages.size - 1)
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = { ForumTopBar(state.messages.size, onNavigateBack) },
        containerColor = Background,
        bottomBar = {
            if (state.isMember) {
                ForumChatInput(
                    message = state.currentMessage,
                    onMessageChange = { viewModel.handleEvent(ForumContract.Event.OnMessageChanged(it)) },
                    onSendClick = { viewModel.handleEvent(ForumContract.Event.OnSendClicked) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading && state.messages.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (!state.isMember) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(Dimens.ContainerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Anda harus bergabung dengan komunitas ini untuk melihat diskusi.",
                        style = BodyLg,
                        color = OnSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else if (state.error != null && state.messages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(Dimens.ContainerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error ?: "Gagal memuat pesan",
                        style = BodyLg,
                        color = Error,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    val pullToRefreshState = rememberPullToRefreshState()
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = {
                            isRefreshing = true
                            viewModel.handleEvent(ForumContract.Event.OnRefresh)
                        },
                        state = pullToRefreshState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = Dimens.ContainerPadding),
                            contentPadding = PaddingValues(top = Dimens.SpacingMd, bottom = Dimens.SpacingXl),
                            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLg)
                        ) {
                            items(state.messages) { message ->
                                val isOutgoing = message.senderId == state.currentUserId
                                if (isOutgoing) {
                                    OutgoingMessage(
                                        message = message.message,
                                        time = message.createdAt.substringAfter("T").take(5),
                                        isRead = true
                                    )
                                } else {
                                    IncomingMessage(
                                        name = message.senderName ?: "User",
                                        nameColor = Primary,
                                        avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=80&h=80",
                                        message = message.message,
                                        time = message.createdAt.substringAfter("T").take(5)
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
fun ForumTopBar(messageCount: Int, onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Forum Diskusi",
                    style = HeadlineMd,
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$messageCount Pesan Terkirim",
                    style = BodySm.copy(fontSize = 12.sp),
                    color = OnSurfaceVariant
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Primary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface.copy(alpha = 0.8f))
    )
}

@Composable
fun IncomingMessage(name: String, nameColor: Color, avatarUrl: String, message: String, time: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = "Avatar",
            modifier = Modifier
                .padding(end = Dimens.SpacingSm, top = Dimens.SpacingXs)
                .size(40.dp)
                .clip(CircleShape)
                .border(2.dp, SurfaceVariant, CircleShape),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.weight(0.8f, fill = false)) {
            Text(text = name, style = BodySm.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold), color = nameColor, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
            Surface(
                shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp),
                color = SurfaceContainerLow,
                shadowElevation = 1.dp
            ) {
                Text(text = message, style = BodyMd.copy(fontSize = 15.sp), color = OnSurface, modifier = Modifier.padding(Dimens.SpacingMd))
            }
            Text(text = time, style = BodySm.copy(fontSize = 10.sp), color = Outline, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
        }
    }
}

@Composable
fun OutgoingMessage(message: String, time: String, isRead: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(0.8f, fill = false), horizontalAlignment = Alignment.End) {
            Surface(
                shape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp),
                color = Primary,
                shadowElevation = 1.dp
            ) {
                Text(text = message, style = BodyMd.copy(fontSize = 15.sp), color = OnPrimary, modifier = Modifier.padding(Dimens.SpacingMd))
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 4.dp, top = 4.dp)) {
                Text(text = time, style = BodySm.copy(fontSize = 10.sp), color = Outline)
                Spacer(modifier = Modifier.width(4.dp))
                if (isRead) {
                    Icon(Icons.Default.DoneAll, contentDescription = "Read", modifier = Modifier.size(14.dp), tint = Primary)
                }
            }
        }
    }
}

@Composable
fun ForumChatInput(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.ContainerPadding, vertical = Dimens.SpacingMd),
        shape = Shapes.Full,
        color = SurfaceBright.copy(alpha = 0.95f),
        shadowElevation = 4.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Dimens.SpacingSm, vertical = Dimens.SpacingSm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = message,
                onValueChange = onMessageChange,
                placeholder = { Text("Ketik pesan...", style = BodyMd, color = OnSurfaceVariant.copy(alpha = 0.7f)) },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                maxLines = 4
            )
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.Mood, contentDescription = "Emoji", tint = OnSurfaceVariant)
            }
            IconButton(
                onClick = onSendClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(Primary, CircleShape)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = OnPrimary, modifier = Modifier.size(20.dp))
            }
        }
    }
}
