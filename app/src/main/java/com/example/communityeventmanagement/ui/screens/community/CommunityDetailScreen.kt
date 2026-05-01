package com.example.communityeventmanagement.ui.screens.community

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.communityeventmanagement.data.model.UserProfile
import com.example.communityeventmanagement.data.repository.AppState
import com.example.communityeventmanagement.ui.components.CommunityEventCard
import com.example.communityeventmanagement.ui.components.EmptyState
import com.example.communityeventmanagement.util.CoverImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityDetailScreen(
    communityId: Int,
    currentUser: UserProfile?,
    onNavigateBack: () -> Unit,
    onNavigateToForum: () -> Unit,
    onNavigateToCreateEvent: () -> Unit,
    onNavigateToEventDetail: (Int) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val community = AppState.communities.find { it.id == communityId }
    
    if (community == null) return
    
    val memberCount = community.memberCount
    val organizerProfile = AppState.allUsers.find { (it.id == community.organizerId) || (it.id == community.organizerId.replace("org_", "user_")) }
    val organizerDisplayName = organizerProfile?.name ?: community.organizerName
    val isTrusted = organizerProfile?.isTrusted ?: false

    val isJoined = communityId in AppState.joinedCommunityIds
    val isOwner = community.organizerId == currentUser?.id || currentUser?.role == "Admin"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(community.name, fontWeight = FontWeight.ExtraBold, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    if (isOwner) {
                        IconButton(onClick = onNavigateToCreateEvent) {
                            Icon(Icons.Default.Add, contentDescription = "Tambah Event", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp).clip(RoundedCornerShape(24.dp)).height(240.dp)) {
                    CoverImage(
                        imageUri = community.coverImageUri,
                        modifier = Modifier.fillMaxSize(),
                        placeholder = {
                            Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))))
                        }
                    )
                    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)))))

                    Column(modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)) {
                        Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.White)
                        Spacer(Modifier.height(8.dp))
                        Text(text = community.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        Spacer(Modifier.height(4.dp))
                        Text(text = community.description, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                        Spacer(Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            CommunityStatChip("$memberCount anggota", Icons.Default.Groups)
                            CommunityStatChip("${community.events.size} event", Icons.Default.Event)
                            CommunityStatChip(community.category, Icons.AutoMirrored.Filled.Label)
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(text = "Dikelola oleh", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = organizerDisplayName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                if (isTrusted) {
                                    Icon(Icons.Default.Verified, contentDescription = "Trusted", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (!isOwner) {
                        Button(
                            onClick = {
                                if (currentUser == null) onNavigateToLogin()
                                else AppState.toggleCommunityJoin(communityId)
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (isJoined) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(if (isJoined) Icons.Default.PersonRemove else Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp), tint = if (isJoined) MaterialTheme.colorScheme.error else Color.White)
                            Spacer(Modifier.width(6.dp))
                            Text(text = if (isJoined) "Keluar Komunitas" else if (currentUser == null) "Login untuk Gabung" else "Gabung Komunitas", fontWeight = FontWeight.Bold, color = if (isJoined) MaterialTheme.colorScheme.error else Color.White)
                        }
                    }
                    if (isJoined || isOwner) {
                        OutlinedButton(onClick = onNavigateToForum, modifier = Modifier.weight(if (isOwner) 1f else 0.6f).height(48.dp), shape = RoundedCornerShape(14.dp)) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Forum", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Text(text = "Event Komunitas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
            }

            if (community.events.isEmpty()) {
                item {
                    EmptyState(
                        title = "Belum ada event",
                        modifier = Modifier.padding(20.dp),
                        subtitle = if (isOwner) "Ayo buat event pertamamu!" else "Nantikan event seru dari komunitas ini.",
                        actionLabel = if (isOwner) "Buat Event" else null,
                        onAction = onNavigateToCreateEvent
                    )
                }
            } else {
                items(community.events) { event ->
                    CommunityEventCard(
                        event = event,
                        isRegistered = AppState.registeredEventIds.contains(event.id),
                        onClick = { onNavigateToEventDetail(event.id) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CommunityStatChip(label: String, icon: ImageVector) {
    Surface(shape = RoundedCornerShape(20.dp), color = Color.White.copy(alpha = 0.2f)) {
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}
