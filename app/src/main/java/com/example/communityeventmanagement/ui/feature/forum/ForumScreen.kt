package com.example.communityeventmanagement.ui.feature.forum

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.domain.model.ForumMessage
import com.example.communityeventmanagement.domain.model.User
import com.example.communityeventmanagement.ui.theme.CommunityEventManagementTheme
import com.example.communityeventmanagement.ui.theme.ThemePreviews
import com.example.communityeventmanagement.util.UiEvent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ForumScreen(
    currentUser: User?,
    onNavigateBack: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    viewModel: ForumViewModel = hiltViewModel(),
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> onShowSnackbar(event.message)
            }
        }
    }
    
    ForumContent(
        messages = messages,
        currentUser = currentUser,
        messageText = viewModel.messageText,
        onMessageChange = { viewModel.messageText = it },
        onSendMessage = viewModel::sendMessage,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumContent(
    messages: List<ForumMessage>,
    currentUser: User?,
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.title_forum),
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                        Text(
                            text = stringResource(R.string.subtitle_forum),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = onMessageChange,
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(stringResource(R.string.hint_message)) },
                        shape = MaterialTheme.shapes.medium,
                        maxLines = 4
                    )
                    FilledIconButton(
                        onClick = onSendMessage,
                        modifier = Modifier.size(48.dp),
                        enabled = messageText.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = stringResource(R.string.cd_send),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                val isMe = msg.sender == currentUser?.name
                MessageBubble(msg = msg, isMe = isMe)
            }
        }
    }
}

@Composable
fun MessageBubble(msg: ForumMessage, isMe: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        if (!isMe) {
            Text(
                text = msg.sender,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )
        }
        
        Surface(
            color = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.medium.copy(
                bottomStart = if (isMe) MaterialTheme.shapes.medium.bottomStart else androidx.compose.foundation.shape.CornerSize(4.dp),
                bottomEnd = if (isMe) androidx.compose.foundation.shape.CornerSize(4.dp) else MaterialTheme.shapes.medium.bottomEnd
            )
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                    text = msg.message,
                    color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = msg.time,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isMe) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End).padding(top = 2.dp)
                )
            }
        }
    }
}

@ThemePreviews
@Composable
fun ForumScreenPreview() {
    CommunityEventManagementTheme {
        ForumContent(
            messages = listOf(
                ForumMessage(id = "1", communityId = 1, sender = "Budi", message = "Halo semuanya!", time = "10:00", avatarInitials = "B"),
                ForumMessage(id = "2", communityId = 1, sender = "Andi", message = "Halo Budi!", time = "10:01", avatarInitials = "A"),
                ForumMessage(id = "3", communityId = 1, sender = "User", message = "Ada info gathering baru?", time = "10:05", avatarInitials = "U")
            ),
            currentUser = User("1", "User", "user@mail.com"),
            messageText = "",
            onMessageChange = {},
            onSendMessage = {},
            onNavigateBack = {}
        )
    }
}

