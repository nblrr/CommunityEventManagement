package com.example.communityeventmanagement.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.domain.model.User
import com.example.communityeventmanagement.ui.theme.CommunityEventManagementTheme
import com.example.communityeventmanagement.ui.theme.ThemePreviews

@ThemePreviews
@Composable
fun AppBottomBarPreview() {
    CommunityEventManagementTheme {
        Box(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
            AppBottomBar(
                currentUser = User(
                    id = "1",
                    name = "John Doe",
                    email = "john@example.com"
                ),
                currentRoute = "home",
                onNavigateToHome = {},
                onNavigateToCommunities = {},
                onNavigateToProfile = {}
            )
        }
    }
}

@Composable
fun AppBottomBar(
    currentUser: User?,
    currentRoute: String,
    onNavigateToHome: () -> Unit,
    onNavigateToCommunities: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .glassmorphism(
                shape = RoundedCornerShape(28.dp), 
                alpha = if (isSystemInDarkTheme()) 0.5f else 0.8f
            ),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                selected = currentRoute == "home",
                onClick = onNavigateToHome,
                icon = if (currentRoute == "home") Icons.Default.Home else Icons.Outlined.Home,
                label = stringResource(R.string.nav_home)
            )
            NavItem(
                selected = currentRoute == "communities",
                onClick = onNavigateToCommunities,
                icon = if (currentRoute == "communities") Icons.Default.Groups else Icons.Outlined.Groups,
                label = stringResource(R.string.title_communities)
            )
            
            val isProfile = currentRoute == "profile"
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onNavigateToProfile() }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (currentUser != null) {
                        val avatarSize by animateDpAsState(
                            targetValue = if (isProfile) 28.dp else 24.dp,
                            animationSpec = tween(300),
                            label = "avatarSize"
                        )
                        Box(
                            modifier = Modifier
                                .size(avatarSize)
                                .clip(CircleShape)
                                .background(
                                    if (isProfile) MaterialTheme.colorScheme.primary 
                                    else MaterialTheme.colorScheme.primaryContainer
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!currentUser.avatarUri.isNullOrEmpty()) {
                                AsyncImage(
                                    model = currentUser.avatarUri,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Text(
                                    currentUser.name.take(1).uppercase(),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Black,
                                    color = if (isProfile) MaterialTheme.colorScheme.onPrimary 
                                            else MaterialTheme.colorScheme.primary,
                                    fontSize = if (isProfile) 14.sp else 12.sp
                                )
                            }
                        }
                    } else {
                        Icon(
                            imageVector = if (isProfile) Icons.Default.Person else Icons.Outlined.Person,
                            contentDescription = null,
                            tint = if (isProfile) MaterialTheme.colorScheme.primary 
                                   else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (currentUser != null) stringResource(R.string.nav_profile) else stringResource(R.string.login),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isProfile) MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isProfile) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.NavItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String
) {
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(300),
        label = "color"
    )

    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp
            )
        }
    }
}

