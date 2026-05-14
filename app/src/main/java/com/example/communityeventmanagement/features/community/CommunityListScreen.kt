package com.example.communityeventmanagement.features.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.data.model.UserProfile
import com.example.communityeventmanagement.ui.AppViewModelProvider
import com.example.communityeventmanagement.ui.components.CommunityCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityListScreen(
    currentUser: UserProfile?,
    onNavigateToCommunityDetail: (Int) -> Unit,
    onNavigateToCreateCommunity: () -> Unit,
    viewModel: CommunityListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var searchQuery by remember { mutableStateOf("") }
    val communities = viewModel.communities
    val filteredCommunities = if (searchQuery.isBlank()) communities else communities.filter { it.name.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_communities), fontWeight = FontWeight.Black) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            if (currentUser?.role == "Organizer" || currentUser?.role == "Admin") {
                FloatingActionButton(onClick = onNavigateToCreateCommunity) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.cd_create_community))
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text(stringResource(R.string.search_hint_community)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = MaterialTheme.shapes.medium
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredCommunities) { community ->
                    CommunityCard(
                        community = community,
                        isJoined = viewModel.joinedCommunityIds.contains(community.id),
                        onClick = { onNavigateToCommunityDetail(community.id) }
                    )
                }
            }
        }
    }
}
