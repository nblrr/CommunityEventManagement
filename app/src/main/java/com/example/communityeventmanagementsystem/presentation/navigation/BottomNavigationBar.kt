package com.example.communityeventmanagementsystem.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.communityeventmanagementsystem.ui.theme.Outline
import com.example.communityeventmanagementsystem.ui.theme.Primary
import com.example.communityeventmanagementsystem.ui.theme.PrimaryContainer
import com.example.communityeventmanagementsystem.ui.theme.Surface

sealed class BottomBarDestination(
    val route: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Home : BottomBarDestination(Screen.Home.route, "Home", Icons.Filled.Home)
    object Communities : BottomBarDestination(Screen.CommunityList.route, "Komunitas", Icons.Filled.Group)
    object Events : BottomBarDestination(Screen.EventList.route, "Events", Icons.Filled.CalendarToday)
    object Notifications : BottomBarDestination(Screen.Notifications.route, "Notifikasi", Icons.Filled.Notifications)
    object Profile : BottomBarDestination(Screen.Profile.route, "Profile", Icons.Filled.Person)
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String?
) {
    val items = listOf(
        BottomBarDestination.Home,
        BottomBarDestination.Communities,
        BottomBarDestination.Events,
        BottomBarDestination.Notifications,
        BottomBarDestination.Profile
    )

    NavigationBar(
        containerColor = Surface
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = isSelected,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    indicatorColor = PrimaryContainer.copy(alpha = 0.15f),
                    unselectedIconColor = Outline,
                    unselectedTextColor = Outline
                ),
                onClick = {
                    if (!isSelected) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
