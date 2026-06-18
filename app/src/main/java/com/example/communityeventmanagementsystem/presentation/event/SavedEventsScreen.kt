package com.example.communityeventmanagementsystem.presentation.event

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EventBusy

import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.communityeventmanagementsystem.ui.theme.*
import com.example.communityeventmanagementsystem.core.common.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedEventsScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToEventDetail: (Long) -> Unit = {},
    viewModel: SavedEventsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.handleEvent(SavedEventsContract.Event.LoadSavedEvents)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SavedEventsContract.Effect.NavigateToEventDetail -> {
                    onNavigateToEventDetail(effect.eventId)
                }
            }
        }
    }
    Scaffold(
        topBar = { SavedEventsTopBar(onNavigateBack) },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Dimens.ContainerPadding, vertical = Dimens.SpacingLg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = Dimens.SpacingXl)) {
                Text("Saved Events", style = HeadlineSm, color = OnSurface, modifier = Modifier.padding(bottom = Dimens.SpacingXs))
                Text("Keep track of the events you're interested in.", style = BodyLg, color = OnSurfaceVariant)
            }

            if (state.isLoading) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (state.error != null) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text(state.error ?: "Gagal memuat event.", color = Error, style = BodyLg)
                }
            } else if (state.savedEvents.isEmpty()) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(256.dp)
                            .padding(bottom = Dimens.SpacingLg),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier.fillMaxSize().background(PrimaryContainer.copy(alpha = 0.2f), CircleShape))
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(SurfaceContainerHigh)
                                .border(2.dp, OutlineVariant, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.EventBusy, contentDescription = null, tint = Outline, modifier = Modifier.size(80.dp))
                        }
                    }

                    Text("No Saved Events Yet", style = HeadlineLg, color = OnSurface, modifier = Modifier.padding(bottom = Dimens.SpacingXs))
                    Text(
                        "Your saved events list is currently empty. Discover upcoming conferences, workshops, and meetups in your area.",
                        style = BodyLg,
                        color = OnSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = Dimens.SpacingLg).fillMaxWidth(0.8f)
                    )

                    Button(
                        onClick = { onNavigateBack() },
                        modifier = Modifier.height(48.dp),
                        shape = Shapes.Full,
                        colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnPrimary)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Find Events", style = LabelMd)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)
                ) {
                    items(state.savedEvents) { event ->
                        VerticalEventCard(
                            title = event.title,
                            category = event.categoryName ?: "KATEGORI",
                            location = DateTimeUtils.formatLocation(event.isOnline, event.location),
                            date = DateTimeUtils.formatEventDateTime(event.eventDate, event.eventTime, event.endTime),
                            joined = "${event.attendeeCount}/${event.maxAttendees} Joined",
                            categoryColor = PrimaryFixed,
                            onCategoryColor = Primary,
                            imageUrl = event.coverImageUrl ?: "https://lh3.googleusercontent.com/aida-public/AB6AXuAe0IQOaUJxhip8Otnj11hlrgGCEl2Valw5CsnwUoP7NtEyTgKTL8Blph8C-KuB_I0O6ZuXZtMkU0QeXtkd2gbK9KZgHIFFNtg-tn2aqWXsa_cew8A5W_bbdiKH5TBCswehLUpkZHHYK095qohP5SZ3-GsZ6DcLRyov22nHSzxZ4L57vEeieTdM89ptOHssu5_AhuqaukdXUxWYXh763d70ETioowrR1fX2RXs9dE7bh1DQoKBHHBM1qqcHfiBb5IvkWAKlPNXlFTUP",
                            onClick = {
                                viewModel.handleEvent(SavedEventsContract.Event.OnEventClicked(event.id))
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedEventsTopBar(onNavigateBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Communitix",
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
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface.copy(alpha = 0.8f))
    )
}
