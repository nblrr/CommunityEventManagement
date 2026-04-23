package com.example.communityeventmanagement.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.communityeventmanagement.data.model.Event
import com.example.communityeventmanagement.data.repository.AppState
import com.example.communityeventmanagement.util.CoverImage
import com.example.communityeventmanagement.util.DateFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityEventCard(
    event: Event,
    isJoined: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUpcoming = AppState.isUpcoming(event.date)

    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CoverImage(
                    imageUri = event.coverImageUri,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = {
                        Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(26.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = event.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.outline)
                    Text(text = DateFormatter.formatEventDate(event.date), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                }
            }

            if (isUpcoming) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isJoined) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = if (isJoined) "Terdaftar" else "Lihat",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isJoined) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                ) {
                    Text(
                        text = "Selesai",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
