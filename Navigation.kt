package com.example.communityeventmanagement.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.communityeventmanagement.CommunityApplication
import com.example.communityeventmanagement.features.admin.AdminPanelScreen
import com.example.communityeventmanagement.features.auth.LoginScreen
import com.example.communityeventmanagement.features.auth.RegisterScreen
import com.example.communityeventmanagement.features.community.CommunityDetailScreen
import com.example.communityeventmanagement.features.community.CommunityListScreen
import com.example.communityeventmanagement.features.community.CreateCommunityScreen
import com.example.communityeventmanagement.features.event.CreateEventScreen
import com.example.communityeventmanagement.features.event.EventDetailScreen
import com.example.communityeventmanagement.features.forum.ForumScreen
import com.example.communityeventmanagement.features.home.HomeScreen
import com.example.communityeventmanagement.features.organizer.OrganizerRegisterScreen
import com.example.communityeventmanagement.features.profile.ProfileScreen
import com.example.communityeventmanagement.ui.components.AppBottomBar

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Profile : Screen("profile")
    data object OrganizerRegister : Screen("organizer_register")
    data object CommunityList : Screen("community_list")
    data object CommunityDetail : Screen("community_detail/{communityId}") {
        fun createRoute(communityId: Int) = "community_detail/$communityId"
    }
    data object CreateCommunity : Screen("create_community")
    data object CreateEvent : Screen("create_event/{communityId}") {
        fun createRoute(communityId: Int) = "create_event/$communityId"
    }
    data object EventDetail : Screen("event_detail/{eventId}/{communityId}") {
        fun createRoute(eventId: Int, communityId: Int) = "event_detail/$eventId/$communityId"
    }
    data object Forum : Screen("forum/{communityId}") {
        fun createRoute(communityId: Int) = "forum/$communityId"
    }
    data object AdminPanel : Screen("admin_panel")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val container = (context.applicationContext as CommunityApplication).container
    val userRepository = container.userRepository
    val communityRepository = container.communityRepository
    
    val currentUser = userRepository.currentUser
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    // Handle blocking logic
    LaunchedEffect(currentUser?.isBlocked) {
        if (currentUser?.isBlocked == true) {
            userRepository.logout()
            communityRepository.joinedCommunityIds.clear()
            communityRepository.registeredEventIds.clear()
            navController.navigate(Screen.Home.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val isTopLevel = currentDestination in listOf(Screen.Home.route, Screen.CommunityList.route, Screen.Profile.route)

    Scaffold(
        bottomBar = {
            if (isTopLevel) {
                AppBottomBar(
                    currentUser = currentUser,
                    currentRoute = when (currentDestination) {
                        Screen.Home.route -> "home"
                        Screen.CommunityList.route -> "communities"
                        Screen.Profile.route -> "profile"
                        else -> ""
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToCommunities = {
                        navController.navigate(Screen.CommunityList.route) {
                            popUpTo(Screen.Home.route)
                            launchSingleTop = true
                        }
                    },
                ) {
                    if (currentUser != null) {
                        navController.navigate(Screen.Profile.route) {
                            popUpTo(Screen.Home.route)
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate(Screen.Login.route)
                    }
                }
            }
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    currentUser = currentUser,
                    onNavigateToCommunityList = { navController.navigate(Screen.CommunityList.route) },
                    onNavigateToAdminPanel = { navController.navigate(Screen.AdminPanel.route) },
                    onNavigateToCommunityDetail = { id -> navController.navigate(Screen.CommunityDetail.createRoute(id)) },
                    onNavigateToEventDetail = { eventId, commId -> navController.navigate(Screen.EventDetail.createRoute(eventId, commId)) }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { _ ->
                        navController.popBackStack()
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = { _ ->
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    currentUser = currentUser,
                    onNavigateToOrganizerRegister = { navController.navigate(Screen.OrganizerRegister.route) },
                    onNavigateToCommunityDetail = { id -> navController.navigate(Screen.CommunityDetail.createRoute(id)) },
                    onLogout = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.OrganizerRegister.route) {
                OrganizerRegisterScreen(
                    onRegisterSuccess = { _ ->
                        navController.popBackStack()
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.CommunityList.route) {
                CommunityListScreen(
                    currentUser = currentUser,
                    onNavigateToCommunityDetail = { id -> navController.navigate(Screen.CommunityDetail.createRoute(id)) },
                    onNavigateToCreateCommunity = {
                        if ((currentUser?.role == "Organizer") || (currentUser?.role == "Admin")) {
                            navController.navigate(Screen.CreateCommunity.route)
                        } else {
                            navController.navigate(Screen.Login.route)
                        }
                    }
                )
            }

            composable(
                route = Screen.CommunityDetail.route,
                arguments = listOf(navArgument("communityId") { type = NavType.IntType })
            ) { backStackEntry ->
                val communityId = backStackEntry.arguments?.getInt("communityId") ?: 0
                CommunityDetailScreen(
                    communityId = communityId,
                    currentUser = currentUser,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToForum = { navController.navigate(Screen.Forum.createRoute(communityId)) },
                    onNavigateToCreateEvent = { navController.navigate(Screen.CreateEvent.createRoute(communityId)) },
                    onNavigateToEventDetail = { eventId -> navController.navigate(Screen.EventDetail.createRoute(eventId, communityId)) },
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) }
                )
            }

            composable(Screen.CreateCommunity.route) {
                CreateCommunityScreen(
                    currentUser = currentUser,
                    onCreateSuccess = { newId ->
                        navController.popBackStack()
                        navController.navigate(Screen.CommunityDetail.createRoute(newId))
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.CreateEvent.route,
                arguments = listOf(navArgument("communityId") { type = NavType.IntType })
            ) { backStackEntry ->
                val communityId = backStackEntry.arguments?.getInt("communityId") ?: 0
                CreateEventScreen(
                    communityId = communityId,
                    onCreateSuccess = { navController.popBackStack() },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.EventDetail.route,
                arguments = listOf(
                    navArgument("eventId") { type = NavType.IntType },
                    navArgument("communityId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getInt("eventId") ?: 0
                val communityId = backStackEntry.arguments?.getInt("communityId") ?: 0
                EventDetailScreen(
                    eventId = eventId,
                    communityId = communityId,
                    currentUser = currentUser,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) }
                )
            }

            composable(
                route = Screen.Forum.route,
                arguments = listOf(navArgument("communityId") { type = NavType.IntType })
            ) { backStackEntry ->
                val communityId = backStackEntry.arguments?.getInt("communityId") ?: 0
                ForumScreen(
                    communityId = communityId,
                    currentUser = currentUser,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AdminPanel.route) {
                if (currentUser?.role != "Admin") {
                    LaunchedEffect(Unit) { navController.popBackStack() }
                } else {
                    AdminPanelScreen(onNavigateBack = { navController.popBackStack() })
                }
            }
        }
    }
}
