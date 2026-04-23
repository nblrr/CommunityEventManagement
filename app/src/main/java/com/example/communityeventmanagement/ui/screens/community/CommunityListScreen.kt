package com.example.communityeventmanagement.ui.screens.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.communityeventmanagement.data.model.*
import com.example.communityeventmanagement.data.repository.AppState
import com.example.communityeventmanagement.ui.components.CommunityCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityListScreen(
    currentUser: UserProfile?,
    onNavigateBack: () -> Unit,
    onNavigateToCommunityDetail: (Int) -> Unit,
    onNavigateToCreateCommunity: () -> Unit
) {
    val communities = AppState.communities
    val joinedIds = AppState.joinedCommunityIds

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Semua Komunitas", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        floatingActionButton = {
            if (currentUser?.role == "Organizer" || currentUser?.role == "Admin") {
                ExtendedFloatingActionButton(
                    onClick = onNavigateToCreateCommunity,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Buat Komunitas", fontWeight = FontWeight.Bold) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${communities.size} Komunitas Tersedia", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                    if (joinedIds.isNotEmpty()) {
                        Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                            Text(text = "Diikuti: ${joinedIds.size}", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            items(communities, key = { it.id }) { community ->
                CommunityCard(
                    community = community,
                    isJoined = community.id in joinedIds,
                    onClick = { onNavigateToCommunityDetail(community.id) }
                )
            }
        }
    }
}
