package com.example.communityeventmanagement.ui.screens.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.communityeventmanagement.data.model.*
import com.example.communityeventmanagement.data.repository.AppState
import com.example.communityeventmanagement.ui.components.CommunityCard
import com.example.communityeventmanagement.ui.components.EmptyState

// Screen Daftar Komunitas
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityListScreen(
    currentUser: UserProfile?,
    onNavigateToCommunityDetail: (Int) -> Unit,
    onNavigateToCreateCommunity: () -> Unit
) {
    val communities = AppState.communities
    val joinedIds = AppState.joinedCommunityIds

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Eksplor Komunitas", fontWeight = FontWeight.Black, letterSpacing = (-0.5).sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            if (currentUser?.role == "Organizer" || currentUser?.role == "Admin") {
                ExtendedFloatingActionButton(
                    onClick = onNavigateToCreateCommunity,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Buat Komunitas", fontWeight = FontWeight.Black) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }
    ) { paddingValues ->
        if (communities.isEmpty()) {
            EmptyState(
                title = "Belum ada komunitas",
                modifier = Modifier.padding(paddingValues).fillMaxSize(),
                icon = Icons.Default.Groups,
                subtitle = "Jadilah yang pertama membuat komunitas baru!",
                actionLabel = if (currentUser?.role == "Organizer") "Buat Komunitas" else null,
                onAction = onNavigateToCreateCommunity
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(text = "Temukan Minatmu", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                            Text(text = "${communities.size} Komunitas tersedia", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        }
                        if (joinedIds.isNotEmpty()) {
                            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)) {
                                Text(text = "${joinedIds.size} Diikuti", modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
                items(communities, key = { it.id }) { community ->
                    CommunityCard(community = community, isJoined = community.id in joinedIds, onClick = { onNavigateToCommunityDetail(community.id) })
                }
            }
        }
    }
}
