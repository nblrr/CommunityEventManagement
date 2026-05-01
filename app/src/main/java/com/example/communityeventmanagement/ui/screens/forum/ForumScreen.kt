package com.example.communityeventmanagement.ui.screens.forum

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.communityeventmanagement.data.model.ForumMessage
import com.example.communityeventmanagement.data.model.UserProfile
import com.example.communityeventmanagement.data.repository.AppState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumScreen(
    communityId: Int,
    currentUser: UserProfile?,
    onNavigateBack: () -> Unit
) {
    val community = AppState.communities.find { it.id == communityId }
    val messages = community?.forumMessages ?: emptyList()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    val isBlocked = currentUser?.isBlocked == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            community?.name ?: "Forum Diskusi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1
                        )
                        Text(
                            "${messages.size} pesan · Live",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        bottomBar = {
            if (isBlocked) {
                Surface(tonalElevation = 3.dp, color = MaterialTheme.colorScheme.errorContainer) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Akun kamu diblokir dan tidak bisa mengirim pesan.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            } else {
                MessageInputBar(
                    value = inputText,
                    onValueChange = { inputText = it },
                    enabled = currentUser != null,
                    placeholder = if (currentUser == null) "Login untuk bergabung di forum..." else "Tulis pesan...",
                    onSend = {
                        if (inputText.isNotBlank() && currentUser != null) {
                            val senderName = currentUser.name
                            val initials = senderName.split(" ").take(2)
                                .joinToString("") { it.take(1) }.uppercase()
                            val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                            val newMsg = ForumMessage(
                                sender = senderName,
                                message = inputText.trim(),
                                time = timeStr,
                                avatarInitials = initials
                            )

                            val idx = AppState.communities.indexOfFirst { it.id == communityId }
                            if (idx != -1) {
                                AppState.communities[idx] = AppState.communities[idx].copy(
                                    forumMessages = AppState.communities[idx].forumMessages + newMsg
                                )
                                AppState.saveForumData(communityId)
                            }
                            inputText = ""
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        if (messages.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("💬", fontSize = 40.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Belum ada pesan.", color = MaterialTheme.colorScheme.outline)
                    Text("Jadilah yang pertama!", color = MaterialTheme.colorScheme.outline, style = MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(messages, key = { "${it.sender}_${it.time}_${it.message.take(10)}" }) { msg ->
                    ChatBubble(
                        message = msg,
                        isMe = msg.sender == currentUser?.name
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ForumMessage, isMe: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isMe) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message.avatarInitials,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp
                )
            }
            Spacer(Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            if (!isMe) {
                Text(
                    text = message.sender,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                )
            }
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp, topEnd = 16.dp,
                    bottomStart = if (isMe) 16.dp else 4.dp,
                    bottomEnd = if (isMe) 4.dp else 16.dp
                ),
                color = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = message.message,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = message.time,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f),
                modifier = Modifier.padding(top = 2.dp),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun MessageInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    enabled: Boolean = true,
    placeholder: String = "Tulis pesan..."
) {
    Surface(tonalElevation = 3.dp, color = MaterialTheme.colorScheme.background) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder) },
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.weight(1f),
                maxLines = 3,
                enabled = enabled
            )
            IconButton(
                onClick = onSend,
                enabled = enabled && value.isNotBlank(),
                modifier = Modifier.background(
                    if (enabled && value.isNotBlank()) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    CircleShape
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Kirim", tint = Color.White)
            }
        }
    }
}