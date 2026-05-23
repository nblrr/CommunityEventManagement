package com.example.communityeventmanagement.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.domain.entities.Community
import com.example.communityeventmanagement.domain.entities.Event
import com.example.communityeventmanagement.ui.theme.CommunityEventManagementTheme
import com.example.communityeventmanagement.ui.theme.ThemePreviews
import com.example.communityeventmanagement.util.CoverImage
import com.example.communityeventmanagement.util.DateUtils

@ThemePreviews
@Composable
fun CommunityHorizontalCardPreview() {
    CommunityEventManagementTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CommunityHorizontalCard(
                community = Community(
                    id = 1,
                    name = "Pecinta Kucing",
                    description = "Komunitas berbagi info tentang kucing.",
                    category = "Hobi",
                    organizerId = "1",
                    organizerName = "Budi",
                    memberIds = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
                ),
                isTrusted = true,
                onClick = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@ThemePreviews
@Composable
fun CommunityVerticalCardPreview() {
    CommunityEventManagementTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CommunityVerticalCard(
                community = Community(
                    id = 1,
                    name = "Tech Community",
                    description = "Sharing knowledge about technology and programming. Join us for weekly meetups!",
                    category = "Teknologi",
                    organizerId = "1",
                    organizerName = "Admin",
                    memberCount = 3,
                    memberIds = listOf("1", "2", "3")
                ),
                isJoined = true,
                isTrusted = true,
                onClick = {}
            )
        }
    }
}

@ThemePreviews
@Composable
fun EventCardItemPreview() {
    CommunityEventManagementTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                EventCardItem(
                    event = Event(
                        id = 1,
                        title = "Workshop Android Modern",
                        description = "Belajar Jetpack Compose.",
                        date = "2025-06-20",
                        location = "Gedung Serbaguna",
                        category = "Pendidikan"
                    ),
                    isRegistered = false,
                    onClick = {}
                )
                EventCardItem(
                    event = Event(
                        id = 2,
                        title = "Meetup Kotlin Indonesia",
                        description = "Sharing sesson.",
                        date = "2025-07-15",
                        location = "Online Zoom",
                        category = "Pendidikan"
                    ),
                    isRegistered = true,
                    onClick = {}
                )
            }
        }
    }
}

// Card Dashboard Komunitas (Horizontal)
@Composable
fun CommunityHorizontalCard(
    community: Community,
    modifier: Modifier = Modifier,
    isTrusted: Boolean = false,
    onClick: () -> Unit
) {
    val memberCount = community.memberCount

    Card(
        onClick = onClick,
        modifier = modifier
            .width(220.dp)
            .glassmorphism(shape = MaterialTheme.shapes.large),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    CoverImage(
                        imageUri = community.coverImageUri,
                        modifier = Modifier.fillMaxSize(),
                        placeholder = {
                            Icon(
                                Icons.Default.Groups, 
                                contentDescription = null, 
                                tint = MaterialTheme.colorScheme.primary, 
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    )
                }
                
                if (isTrusted) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.extraSmall,
                        modifier = Modifier.offset(x = 4.dp, y = (-4).dp)
                    ) {
                        Icon(
                            Icons.Default.Verified, 
                            contentDescription = stringResource(R.string.cd_trusted), 
                            tint = MaterialTheme.colorScheme.onPrimary, 
                            modifier = Modifier.padding(4.dp).size(14.dp)
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            Text(
                text = community.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = community.category,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            
            Spacer(Modifier.height(14.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .glassmorphism(shape = MaterialTheme.shapes.small, alpha = 0.4f)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    Icons.Default.Person, 
                    contentDescription = null, 
                    modifier = Modifier.size(12.dp), 
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.member_count_format, memberCount),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Card Komunitas (List)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityVerticalCard(
    community: Community,
    isJoined: Boolean,
    isTrusted: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphism(shape = MaterialTheme.shapes.large),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        CoverImage(
                            imageUri = community.coverImageUri,
                            modifier = Modifier.fillMaxSize(),
                            placeholder = {
                                Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.primary)
                            }
                        )
                    }
                    Column {
                        Text(text = community.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                        Surface(
                            shape = MaterialTheme.shapes.extraSmall, 
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = community.category.uppercase(),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Black,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
                
                if (isJoined) {
                    // Badge
                    Badge(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.clip(MaterialTheme.shapes.extraSmall)
                    ) {
                        Text(stringResource(R.string.status_followed), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = community.description, 
                style = MaterialTheme.typography.bodySmall, 
                color = MaterialTheme.colorScheme.onSurfaceVariant, 
                maxLines = 2,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    InfoItem(icon = Icons.Default.Groups, text = community.memberCount.toString(), color = MaterialTheme.colorScheme.primary)
                    InfoItem(icon = Icons.Default.Event, text = community.events.size.toString(), color = MaterialTheme.colorScheme.secondary)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (isTrusted) {
                        Icon(Icons.Default.Verified, contentDescription = stringResource(R.string.cd_trusted), tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                    }
                    Text(
                        text = community.organizerName, 
                        style = MaterialTheme.typography.labelSmall, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoItem(icon: ImageVector, text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = color)
        Text(text = text, style = MaterialTheme.typography.labelMedium, color = color, fontWeight = FontWeight.ExtraBold)
    }
}

// Card Event Komunitas
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCardItem(
    event: Event,
    isRegistered: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUpcoming = remember(event.date) {
        DateUtils.isUpcoming(event.date)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .glassmorphism(shape = MaterialTheme.shapes.large),
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                CoverImage(
                    imageUri = event.coverImageUri,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = {
                        Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = event.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                    Text(text = DateUtils.formatEventDate(event.date), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = event.location, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            if (isUpcoming) {
                Surface(
                    modifier = Modifier.glassmorphism(shape = MaterialTheme.shapes.small, alpha = 0.2f),
                    color = Color.Transparent
                ) {
                    Text(
                        text = if (isRegistered) stringResource(R.string.status_registered) else stringResource(R.string.btn_register),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isRegistered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            } else {
                Surface(
                    modifier = Modifier.glassmorphism(shape = MaterialTheme.shapes.small, alpha = 0.1f),
                    color = Color.Transparent
                ) {
                    Text(
                        text = stringResource(R.string.status_finished),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
