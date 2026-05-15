package com.example.communityeventmanagement.features.community

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.data.model.Community
import com.example.communityeventmanagement.data.model.UserProfile
import com.example.communityeventmanagement.ui.AppViewModelProvider
import com.example.communityeventmanagement.ui.components.CommunityCard
import com.example.communityeventmanagement.ui.theme.CommunityEventManagementTheme
import com.example.communityeventmanagement.ui.theme.ThemePreviews

@Composable
fun CommunityListScreen(
    currentUser: UserProfile?,
    onNavigateToCommunityDetail: (Int) -> Unit,
    onNavigateToCreateCommunity: () -> Unit,
    viewModel: CommunityListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val communities = viewModel.communities
    val joinedCommunityIds = viewModel.joinedCommunityIds

    CommunityListContent(
        currentUser = currentUser,
        communities = communities,
        joinedCommunityIds = joinedCommunityIds,
        onNavigateToCommunityDetail = onNavigateToCommunityDetail,
        onNavigateToCreateCommunity = onNavigateToCreateCommunity
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityListContent(
    currentUser: UserProfile?,
    communities: List<Community>,
    joinedCommunityIds: List<Int>,
    onNavigateToCommunityDetail: (Int) -> Unit,
    onNavigateToCreateCommunity: () -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredCommunities = remember(searchQuery, communities) {
        if (searchQuery.isBlank()) communities 
        else communities.filter { it.name.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true) }
    }

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
                items(filteredCommunities, key = { it.id }) { community ->
                    CommunityCard(
                        community = community,
                        isJoined = joinedCommunityIds.contains(community.id),
                        onClick = { onNavigateToCommunityDetail(community.id) }
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
fun CommunityListScreenPreview() {
    CommunityEventManagementTheme {
        CommunityListContent(
            currentUser = UserProfile(id = "1", name = "Admin", email = "admin@ex.com", role = "Admin"),
            communities = listOf(
                Community(1, "Tech Talk", "Discussing tech.", "Teknologi", null, "1", "Admin"),
                Community(2, "Sport Club", "Playing sports.", "Hobi", null, "1", "Admin")
            ),
            joinedCommunityIds = listOf(1),
            onNavigateToCommunityDetail = {},
            onNavigateToCreateCommunity = {}
        )
    }
}
