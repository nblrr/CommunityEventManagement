package com.example.communityeventmanagement.features.event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.data.model.Event
import com.example.communityeventmanagement.data.model.Rating
import com.example.communityeventmanagement.data.model.UserProfile
import com.example.communityeventmanagement.ui.AppViewModelProvider
import com.example.communityeventmanagement.ui.theme.CommunityEventManagementTheme
import com.example.communityeventmanagement.ui.theme.ThemePreviews
import com.example.communityeventmanagement.util.CoverImage

@Composable
fun EventDetailScreen(
    eventId: Int,
    communityId: Int,
    currentUser: UserProfile?,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: EventDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(eventId, communityId) {
        viewModel.loadEvent(eventId, communityId)
    }

    val event = viewModel.event ?: return
    val isRegistered = viewModel.isRegistered
    val isOrganizer = viewModel.isOrganizer(currentUser?.id)
    val isUpcoming = viewModel.isUpcoming()

    EventDetailContent(
        event = event,
        communityName = viewModel.communityName,
        isRegistered = isRegistered,
        isOrganizer = isOrganizer,
        isUpcoming = isUpcoming,
        currentUser = currentUser,
        onToggleRegistration = { viewModel.toggleRegistration(communityId, eventId, currentUser?.id ?: "") },
        onNavigateBack = onNavigateBack,
        onNavigateToLogin = onNavigateToLogin
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailContent(
    event: Event,
    communityName: String,
    isRegistered: Boolean,
    isOrganizer: Boolean,
    isUpcoming: Boolean,
    currentUser: UserProfile?,
    onToggleRegistration: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    var showRatingDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_detail_event), fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Header Image
            item {
                Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                    CoverImage(imageUri = event.coverImageUri, modifier = Modifier.fillMaxSize())
                    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)))))
                    
                    Surface(
                        modifier = Modifier.padding(16.dp).align(Alignment.TopEnd),
                        color = if (isUpcoming) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            if (isUpcoming) stringResource(R.string.status_upcoming) else stringResource(R.string.status_finished),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Info Section
            item {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(event.category.uppercase(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(event.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, modifier = Modifier.padding(vertical = 8.dp))
                    
                    TextButton(onClick = {}, contentPadding = PaddingValues(0.dp)) {
                        Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(communityName, fontWeight = FontWeight.Bold)
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 20.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    InfoRow(icon = Icons.Default.CalendarToday, title = event.date, subtitle = event.time)
                    Spacer(Modifier.height(16.dp))
                    InfoRow(icon = Icons.Default.LocationOn, title = event.location, subtitle = stringResource(R.string.label_view_on_map))

                    Spacer(Modifier.height(32.dp))
                    Text(stringResource(R.string.section_about_event), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Text(event.description, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp), lineHeight = 24.sp)

                    if (!isUpcoming && isRegistered && !isOrganizer) {
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = { showRatingDialog = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                            Icon(Icons.Default.Star, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.btn_rate_review))
                        }
                    }
                }
            }

            // Gallery
            if (!event.galleryImages.isNullOrEmpty()) {
                item {
                    Text(stringResource(R.string.section_gallery), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp))
                }
                item {
                    androidx.compose.foundation.lazy.LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(event.galleryImages!!) { img ->
                            Box(modifier = Modifier.size(120.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
                                CoverImage(imageUri = img, modifier = Modifier.fillMaxSize())
                            }
                        }
                    }
                }
            }

            // Ratings
            if (!event.ratings.isNullOrEmpty()) {
                item {
                    Text(
                        stringResource(R.string.section_participant_reviews),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 16.dp)
                    )
                }
                items(event.ratings!!) { rating ->
                    RatingItem(rating = rating)
                }
            }
        }
    }

    // Bottom Action
    if (isUpcoming && !isOrganizer) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 16.dp) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.label_free), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                        Text(stringResource(R.string.attendee_count_format, event.attendeeCount), style = MaterialTheme.typography.labelMedium)
                    }
                    Button(
                        onClick = { 
                            if (currentUser == null) onNavigateToLogin()
                            else onToggleRegistration()
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.height(52.dp).padding(horizontal = 8.dp),
                        colors = if (isRegistered) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant) else ButtonDefaults.buttonColors()
                    ) {
                        Text(if (isRegistered) stringResource(R.string.btn_cancel_registration) else stringResource(R.string.btn_register_event), fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
private fun RatingItem(rating: Rating) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer), contentAlignment = Alignment.Center) {
                Text(rating.userName.take(1).uppercase(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(rating.userName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Row {
                    repeat(5) { i ->
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(12.dp), tint = if (i < rating.score) Color(0xFFFFB300) else Color.LightGray)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(rating.date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
        Text(rating.comment, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp, start = 44.dp))
    }
}

@ThemePreviews
@Composable
fun EventDetailScreenPreview() {
    CommunityEventManagementTheme {
        EventDetailContent(
            event = Event(
                id = 1,
                title = "Workshop Android Modern",
                description = "Pelajari cara membangun aplikasi Android menggunakan Jetpack Compose dan Material 3.",
                date = "2025-06-25",
                time = "10:00 - 15:00",
                location = "Gedung Serbaguna Lt. 2",
                category = "Pendidikan",
                maxAttendees = 100,
                registeredUserIds = listOf("1", "2", "3"),
                ratings = listOf(
                    Rating("1", "Budi", 5, "Workshop yang sangat bermanfaat!", "2025-06-26")
                )
            ),
            communityName = "Tech Community",
            isRegistered = false,
            isOrganizer = false,
            isUpcoming = true,
            currentUser = UserProfile("2", "Andi", "andi@mail.com"),
            onToggleRegistration = {},
            onNavigateBack = {},
            onNavigateToLogin = {}
        )
    }
}
