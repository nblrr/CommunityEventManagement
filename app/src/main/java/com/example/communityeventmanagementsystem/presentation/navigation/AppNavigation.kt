package com.example.communityeventmanagementsystem.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.communityeventmanagementsystem.presentation.main.MainViewModel

import com.example.communityeventmanagementsystem.presentation.admin.AdminDashboardScreen
import com.example.communityeventmanagementsystem.presentation.auth.LoginScreen
import com.example.communityeventmanagementsystem.presentation.auth.RegisterScreen
import com.example.communityeventmanagementsystem.presentation.community.CommunityDetailScreen
import com.example.communityeventmanagementsystem.presentation.community.CommunityListScreen
import com.example.communityeventmanagementsystem.presentation.event.EventDetailScreen
import com.example.communityeventmanagementsystem.presentation.event.EventListScreen
import com.example.communityeventmanagementsystem.presentation.forum.ForumScreen
import com.example.communityeventmanagementsystem.presentation.home.HomeScreen
import com.example.communityeventmanagementsystem.presentation.notifications.NotificationScreen
import com.example.communityeventmanagementsystem.presentation.organizer.OrganizerDashboardScreen
import com.example.communityeventmanagementsystem.presentation.profile.EditProfileScreen
import com.example.communityeventmanagementsystem.presentation.profile.ProfileScreen
import com.example.communityeventmanagementsystem.presentation.trusted.TrustedAppScreen
import com.example.communityeventmanagementsystem.presentation.community.CreateCommunityScreen
import com.example.communityeventmanagementsystem.presentation.event.CreateEventScreen
import com.example.communityeventmanagementsystem.presentation.event.SavedEventsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    viewModel: MainViewModel = hiltViewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    if (isLoggedIn == null) return

    val startDestination = remember(isLoggedIn) {
        if (isLoggedIn == true) Screen.Home.route else Screen.Login.route
    }

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.CommunityList.route,
        Screen.EventList.route,
        Screen.Notifications.route,
        Screen.Profile.route
    ) || currentRoute?.startsWith("community_list") == true || currentRoute?.startsWith("event_list") == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = hiltViewModel(),
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    viewModel = hiltViewModel(),
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToCategory = { catId -> navController.navigate(Screen.EventList.createRoute(catId)) },
                    onNavigateToEventDetail = { eventId -> navController.navigate(Screen.EventDetail(eventId).route) },
                    onNavigateToCommunityDetail = { commId -> navController.navigate(Screen.CommunityDetail(commId).route) },
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                    onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                    onNavigateToCreateCommunity = { navController.navigate(Screen.CreateCommunity.route) },
                    onNavigateToCreateEvent = { navController.navigate(Screen.CreateEvent.route) },
                    onNavigateToSearchAndFilter = { navController.navigate(Screen.EventList.createRoute(-1L)) },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable(
                route = Screen.CommunityList.route,
                arguments = listOf(navArgument("categoryId") {
                    type = NavType.LongType
                    defaultValue = -1L
                })
            ) {
                CommunityListScreen(
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToCommunityDetail = { id -> navController.navigate(Screen.CommunityDetail(id).route) }
                )
            }
            composable(
                route = Screen.EventList.route,
                arguments = listOf(navArgument("categoryId") {
                    type = NavType.LongType
                    defaultValue = -1L
                })
            ) {
                EventListScreen(
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToEventDetail = { id -> navController.navigate(Screen.EventDetail(id).route) }
                )
            }
            composable(
                route = Screen.CommunityDetail.ROUTE,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) {
                CommunityDetailScreen(
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToForum = { id -> navController.navigate(Screen.CommunityForum(id).route) },
                    onNavigateToCreateEvent = { commId -> 
                        navController.navigate(Screen.CreateEvent.route + "?communityId=$commId")
                    }
                )
            }
            composable(
                route = Screen.CommunityForum.ROUTE,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) {
                ForumScreen(onNavigateBack = { navController.navigateUp() })
            }
            composable(
                route = Screen.EventDetail.ROUTE,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) {
                EventDetailScreen(
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Notifications.route) {
                NotificationScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.navigateUp() }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) },
                    onNavigateToSavedEvents = { navController.navigate(Screen.SavedEvents.route) },
                    onNavigateToTrustedApp = { navController.navigate(Screen.TrustedApplication.route) },
                    onNavigateToOrganizerDashboard = { navController.navigate(Screen.OrganizerDashboard.route) },
                    onNavigateToAdminDashboard = { navController.navigate(Screen.AdminDashboard.route) },
                    onLogoutSuccess = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.EditProfile.route) {
                // Since EditProfile is currently receiving viewModel and onNavigateBack in its original definition, 
                // wait, EditProfileScreen was updated by ME to take (viewModel, onNavigateBack). 
                // Wait! If EditProfileScreen takes arguments, I need to pass them or update EditProfileScreen.
                // Let me just pass the hiltViewModel for EditProfileScreen to prevent build failures.
                val parentEntry = remember(it) {
                    navController.getBackStackEntry(Screen.Profile.route)
                }
                val viewModel: com.example.communityeventmanagementsystem.presentation.profile.ProfileViewModel = hiltViewModel(parentEntry)
                EditProfileScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.navigateUp() }
                )
            }
            composable(Screen.TrustedApplication.route) {
                TrustedAppScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.navigateUp() }
                )
            }
            composable(Screen.OrganizerDashboard.route) {
                OrganizerDashboardScreen(
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToCreateCommunity = { navController.navigate(Screen.CreateCommunity.route) },
                    onNavigateToCreateEvent = { navController.navigate(Screen.CreateEvent.route) },
                    onNavigateToCommunityDetail = { id -> navController.navigate(Screen.CommunityDetail(id).route) },
                    onNavigateToEventDetail = { id -> navController.navigate(Screen.EventDetail(id).route) }
                )
            }
            composable(Screen.AdminDashboard.route) {
                AdminDashboardScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.navigateUp() }
                )
            }
            composable(Screen.SavedEvents.route) {
                SavedEventsScreen(
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToEventDetail = { id -> navController.navigate(Screen.EventDetail(id).route) }
                )
            }
            composable(Screen.CreateCommunity.route) {
                CreateCommunityScreen(onNavigateBack = { navController.navigateUp() })
            }
            composable(
                route = Screen.CreateEvent.route + "?communityId={communityId}",
                arguments = listOf(navArgument("communityId") {
                    type = NavType.LongType
                    defaultValue = -1L
                })
            ) { backStackEntry ->
                val communityId = backStackEntry.arguments?.getLong("communityId")?.takeIf { it != -1L }
                CreateEventScreen(
                    onNavigateBack = { navController.navigateUp() },
                    communityIdPrefill = communityId
                )
            }
        }
    }
}
