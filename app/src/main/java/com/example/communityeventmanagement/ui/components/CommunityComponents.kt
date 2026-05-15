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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.data.model.Community
import com.example.communityeventmanagement.data.model.Event
import com.example.communityeventmanagement.ui.theme.CommunityEventManagementTheme
import com.example.communityeventmanagement.ui.theme.ThemePreviews
import com.example.communityeventmanagement.util.CoverImage
import com.example.communityeventmanagement.util.DateFormatter

@ThemePreviews
@Composable
fun CommunityDashboardCardPreview() {
    CommunityEventManagementTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CommunityDashboardCard(
                community = Community(
                    id = 1,
                    name = "Pecinta Kucing",
                    description = "Komunitas berbagi info tentang kucing.",
                    category = "Hobi",
                    organizerId = "1",
                    organizerName = "Budi"
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
fun CommunityCardPreview() {
    CommunityEventManagementTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CommunityCard(
                community = Community(
                    id = 1,
                    name = "Tech Community",
                    description = "Sharing knowledge about technology and programming. Join us for weekly meetups!",
                    category = "Teknologi",
                    organizerId = "1",
                    organizerName = "Admin",
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
fun CommunityEventCardPreview() {
    CommunityEventManagementTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                CommunityEventCard(
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
                CommunityEventCard(
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
fun CommunityDashboardCard(
    community: Community,
    modifier: Modifier = Modifier,
    isTrusted: Boolean = false,
    onClick: () -> Unit
) {
    val memberCount = remember(community) {
        community.events.flatMap { it.registeredUserIds }.distinct().size
    }

    Card(
        onClick = onClick,
        modifier = modifier.width(200.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp, 
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                )
                            )
                        ),
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
                        shape = RoundedCornerShape(8.dp),
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
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    Icons.Default.Person, 
                    contentDescription = null, 
                    modifier = Modifier.size(12.dp), 
                    tint = MaterialTheme.colorScheme.outline
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
fun CommunityCard(
    community: Community,
    isJoined: Boolean,
    isTrusted: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
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
                            shape = RoundedCornerShape(6.dp), 
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
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
                        contentColor = MaterialTheme.colorScheme.secondary
                    ) {
                        Text(stringResource(R.string.status_followed), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = community.description, 
                style = MaterialTheme.typography.bodySmall, 
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), 
                maxLines = 2,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
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
                        color = MaterialTheme.colorScheme.outline,
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
fun CommunityEventCard(
    event: Event,
    isRegistered: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUpcoming = remember(event.date) {
        DateFormatter.isUpcoming(event.date)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = if (isRegistered) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(16.dp))
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
                    Text(text = DateFormatter.formatEventDate(event.date), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.outline)
                    Text(text = event.location, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            if (isUpcoming) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isRegistered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ) {
                    Text(
                        text = if (isRegistered) stringResource(R.string.status_registered) else stringResource(R.string.btn_register),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isRegistered) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp), 
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = stringResource(R.string.status_finished),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
