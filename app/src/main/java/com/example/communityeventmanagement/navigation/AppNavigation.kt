package com.example.communityeventmanagement.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.communityeventmanagement.data.AppState
import com.example.communityeventmanagement.data.UserProfile
import com.example.communityeventmanagement.ui.screens.auth.LoginScreen
import com.example.communityeventmanagement.ui.screens.auth.RegisterScreen
import com.example.communityeventmanagement.ui.screens.community.CommunityDetailScreen
import com.example.communityeventmanagement.ui.screens.community.CommunityListScreen
import com.example.communityeventmanagement.ui.screens.community.CreateCommunityScreen
import com.example.communityeventmanagement.ui.screens.event.CreateEventScreen
import com.example.communityeventmanagement.ui.screens.forum.ForumScreen
import com.example.communityeventmanagement.ui.screens.home.HomeScreen
import com.example.communityeventmanagement.ui.screens.organizer.OrganizerRegisterScreen
import com.example.communityeventmanagement.ui.screens.profile.ProfileScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // State login & user disimpan di sini sebagai single source of truth
    var currentUser by remember { mutableStateOf(AppState.currentUser) }

    // Helper untuk update user dan sync ke AppState
    fun updateUser(user: UserProfile?) {
        AppState.currentUser = user
        currentUser = user
    }

    NavHost(navController = navController, startDestination = "home") {

        // ── Home ──────────────────────────────────────────────────────────────
        composable("home") {
            HomeScreen(
                currentUser = currentUser,
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToCommunityList = { navController.navigate("community_list") }
            )
        }

        // ── Auth ──────────────────────────────────────────────────────────────
        composable("login") {
            LoginScreen(
                onLoginSuccess = { user ->
                    updateUser(user)
                    navController.popBackStack("home", inclusive = false)
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { user ->
                    updateUser(user)
                    navController.popBackStack("home", inclusive = false)
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // ── Profile ───────────────────────────────────────────────────────────
        composable("profile") {
            ProfileScreen(
                currentUser = currentUser,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToOrganizerRegister = { navController.navigate("organizer_register") },
                onLogout = {
                    updateUser(null)
                    AppState.joinedCommunityIds.clear()
                    navController.popBackStack("home", inclusive = false)
                }
            )
        }

        // ── Organizer Register ────────────────────────────────────────────────
        composable("organizer_register") {
            OrganizerRegisterScreen(
                currentUser = currentUser,
                onRegisterSuccess = { updatedUser ->
                    updateUser(updatedUser)
                    navController.popBackStack("profile", inclusive = false)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Community List ────────────────────────────────────────────────────
        composable("community_list") {
            CommunityListScreen(
                currentUser = currentUser,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCommunityDetail = { communityId ->
                    navController.navigate("community_detail/$communityId")
                },
                onNavigateToCreateCommunity = { navController.navigate("create_community") }
            )
        }

        // ── Community Detail ──────────────────────────────────────────────────
        composable("community_detail/{communityId}") { backStackEntry ->
            val communityId = backStackEntry.arguments?.getString("communityId")?.toIntOrNull() ?: 0
            CommunityDetailScreen(
                communityId = communityId,
                currentUser = currentUser,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToForum = { navController.navigate("forum/$communityId") },
                onNavigateToCreateEvent = { navController.navigate("create_event/$communityId") },
                onNavigateToLogin = { navController.navigate("login") }
            )
        }

        // ── Create Community ──────────────────────────────────────────────────
        composable("create_community") {
            CreateCommunityScreen(
                currentUser = currentUser,
                onCreateSuccess = { communityId ->
                    navController.navigate("community_detail/$communityId") {
                        popUpTo("community_list") { inclusive = false }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Create Event ──────────────────────────────────────────────────────
        composable("create_event/{communityId}") { backStackEntry ->
            val communityId = backStackEntry.arguments?.getString("communityId")?.toIntOrNull() ?: 0
            CreateEventScreen(
                communityId = communityId,
                currentUser = currentUser,
                onCreateSuccess = {
                    navController.popBackStack()
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Forum ─────────────────────────────────────────────────────────────
        composable("forum/{communityId}") { backStackEntry ->
            val communityId = backStackEntry.arguments?.getString("communityId")?.toIntOrNull() ?: 0
            ForumScreen(
                communityId = communityId,
                currentUser = currentUser,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}