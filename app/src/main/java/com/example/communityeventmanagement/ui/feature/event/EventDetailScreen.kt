package com.example.communityeventmanagement.ui.feature.event

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.domain.model.AppCategories
import com.example.communityeventmanagement.domain.model.Event
import com.example.communityeventmanagement.domain.model.Rating
import com.example.communityeventmanagement.domain.model.User
import com.example.communityeventmanagement.domain.model.findDisplayRes
import com.example.communityeventmanagement.ui.components.FullScreenLoading
import com.example.communityeventmanagement.ui.theme.CommunityEventManagementTheme
import com.example.communityeventmanagement.ui.theme.ThemePreviews
import com.example.communityeventmanagement.util.CoverImage
import com.example.communityeventmanagement.util.DateUtils

@Composable
fun EventDetailScreen(
    currentUser: User?,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToEditEvent: (Int, Int) -> Unit,
    viewModel: EventDetailViewModel = hiltViewModel()
) {
    val event by viewModel.event.collectAsStateWithLifecycle()
    val community by viewModel.community.collectAsStateWithLifecycle()
    
    if (event == null) {
        FullScreenLoading()
        return
    }
    
    val registeredIds by viewModel.registeredEventIds.collectAsStateWithLifecycle()
    val isRegistered = registeredIds.contains(event!!.id)
    val isOrganizer = currentUser?.id == community?.organizerId
    val isUpcoming = DateUtils.isUpcoming(event!!.date, event!!.time)
    val isFull = event!!.maxAttendees > 0 && event!!.attendeeCount >= event!!.maxAttendees

    EventDetailContent(
        event = event!!,
        communityName = community?.name ?: "",
        isRegistered = isRegistered,
        isOrganizer = isOrganizer,
        isUpcoming = isUpcoming,
        isFull = isFull,
        currentUser = currentUser,
        onToggleRegistration = { viewModel.toggleRegistration() },
        onNavigateBack = onNavigateBack,
        onNavigateToLogin = onNavigateToLogin,
        onEditEvent = { onNavigateToEditEvent(event!!.id, event!!.communityId) },
        onDeleteEvent = { viewModel.deleteEvent(onNavigateBack) },
        onSubmitRating = { score, comment -> viewModel.submitRating(score, comment) }
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
    isFull: Boolean,
    currentUser: User?,
    onToggleRegistration: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onEditEvent: () -> Unit,
    onDeleteEvent: () -> Unit,
    onSubmitRating: (Int, String) -> Unit,
) {
    var showRatingDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_detail_event), fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    if (isOrganizer) {
                        var expanded by remember { mutableStateOf(false) }
                        Box {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = null)
                            }
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.action_edit)) },
                                    onClick = { 
                                        expanded = false
                                        onEditEvent() 
                                    },
                                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.action_delete)) },
                                    onClick = { 
                                        expanded = false
                                        showDeleteDialog = true 
                                    },
                                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                                    colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.error)
                                )
                            }
                        }
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
            item {
                Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                    CoverImage(imageUri = event.coverImageUri, modifier = Modifier.fillMaxSize())
                    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black))))
                    
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

            item {
                Column(modifier = Modifier.padding(24.dp)) {
                    val categoryName = AppCategories.findDisplayRes(event.category)?.let { stringResource(it) } ?: event.category
                    Text(categoryName.uppercase(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(event.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, modifier = Modifier.padding(vertical = 8.dp))
                    
                    TextButton(onClick = {}, contentPadding = PaddingValues(0.dp)) {
                        Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(communityName, fontWeight = FontWeight.Bold)
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 20.dp),
                        color = MaterialTheme.colorScheme.outline
                    )

                    InfoRow(icon = Icons.Default.CalendarToday, title = DateUtils.formatEventDate(event.date), subtitle = event.time)
                    Spacer(Modifier.height(16.dp))
                    InfoRow(icon = Icons.Default.LocationOn, title = event.location, subtitle = "")

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

            if (event.galleryImages.isNotEmpty()) {
                item {
                    Text(stringResource(R.string.section_gallery), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp))
                }
                item {
                    androidx.compose.foundation.lazy.LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(event.galleryImages) { img ->
                            Box(modifier = Modifier.size(120.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
                                CoverImage(imageUri = img, modifier = Modifier.fillMaxSize())
                            }
                        }
                    }
                }
            }

            if (event.ratings.isNotEmpty()) {
                item {
                    Text(
                        stringResource(R.string.section_participant_reviews),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 16.dp)
                    )
                }
                items(event.ratings) { rating ->
                    RatingItem(rating = rating)
                }
            }
        }
    }

    if (isUpcoming && !isOrganizer) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 16.dp) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.label_free), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                        val attendeeCountText = if (event.maxAttendees > 0) {
                            stringResource(R.string.quota_format, event.attendeeCount, event.maxAttendees)
                        } else {
                            stringResource(R.string.attendee_count_format, event.attendeeCount)
                        }
                        Text(attendeeCountText, style = MaterialTheme.typography.labelMedium)
                    }
                    Button(
                        onClick = { 
                            if (currentUser == null) onNavigateToLogin()
                            else onToggleRegistration()
                        },
                        enabled = !isFull || isRegistered,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.height(52.dp).padding(horizontal = 8.dp),
                        colors = if (isRegistered) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant) else ButtonDefaults.buttonColors()
                    ) {
                        val buttonText = when {
                            isRegistered -> stringResource(R.string.btn_cancel_registration)
                            isFull -> stringResource(R.string.label_quota_full)
                            else -> stringResource(R.string.btn_register_event)
                        }
                        Text(buttonText, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.dialog_delete_event_title)) },
            text = { Text(stringResource(R.string.dialog_delete_event_msg)) },
            confirmButton = {
                TextButton(onClick = { 
                    showDeleteDialog = false
                    onDeleteEvent() 
                }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Text(stringResource(R.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }

    if (showRatingDialog) {
        var score by remember { mutableIntStateOf(5) }
        var comment by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showRatingDialog = false },
            title = { Text(stringResource(R.string.title_rating_dialog)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(stringResource(R.string.label_rating_score))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(5) { index ->
                            val starScore = index + 1
                            IconButton(onClick = { score = starScore }) {
                                Icon(
                                    imageVector = if (score >= starScore) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    tint = if (score >= starScore) Color(0xFFFFB300) else Color.LightGray,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        placeholder = { Text(stringResource(R.string.label_comment_hint)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSubmitRating(score, comment)
                        showRatingDialog = false
                    },
                    enabled = comment.isNotBlank()
                ) {
                    Text(stringResource(R.string.btn_submit_rating))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRatingDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}

@Composable
private fun InfoRow(icon: ImageVector, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            if (subtitle.isNotEmpty()) {
                Text(subtitle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
            }
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
                date = "25 6 2025",
                time = "10.00 - 15.00",
                location = "Gedung Serbaguna Lt. 2",
                category = "EDUCATION",
                maxAttendees = 20,
                attendeeCount = 20,
                registeredUserIds = List(20) { it.toString() },
                ratings = listOf(
                    Rating("1", "Budi", 5, "Workshop yang sangat bermanfaat!", "26 6 2025")
                )
            ),
            communityName = "Tech Community",
            isRegistered = false,
            isOrganizer = false,
            isUpcoming = true,
            isFull = true,
            currentUser = User("2", "Andi", "andi@mail.com"),
            onToggleRegistration = {},
            onNavigateBack = {},
            onNavigateToLogin = {},
            onEditEvent = {},
            onDeleteEvent = {},
            onSubmitRating = { _, _ -> }
        )
    }
}

