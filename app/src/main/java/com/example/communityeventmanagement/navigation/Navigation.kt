package com.example.communityeventmanagement.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.communityeventmanagement.domain.model.UserRole
import com.example.communityeventmanagement.ui.MainViewModel
import com.example.communityeventmanagement.ui.components.AppBottomBar
import com.example.communityeventmanagement.ui.feature.admin.AdminPanelScreen
import com.example.communityeventmanagement.ui.feature.auth.LoginScreen
import com.example.communityeventmanagement.ui.feature.auth.RegisterScreen
import com.example.communityeventmanagement.ui.feature.community.CommunityDetailScreen
import com.example.communityeventmanagement.ui.feature.community.CommunityListScreen
import com.example.communityeventmanagement.ui.feature.community.CreateCommunityScreen
import com.example.communityeventmanagement.ui.feature.event.CreateEventScreen
import com.example.communityeventmanagement.ui.feature.event.EventDetailScreen
import com.example.communityeventmanagement.ui.feature.forum.ForumScreen
import com.example.communityeventmanagement.ui.feature.home.HomeScreen
import com.example.communityeventmanagement.ui.feature.organizer.OrganizerRegisterScreen
import com.example.communityeventmanagement.ui.feature.profile.EditProfileScreen
import com.example.communityeventmanagement.ui.feature.profile.ProfileScreen
import com.example.communityeventmanagement.ui.feature.profile.TrustedOrganizerApplyScreen
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Profile : Screen("profile")
    data object EditProfile : Screen("edit_profile")
    data object OrganizerRegister : Screen("organizer_register")
    data object TrustedOrganizerApply : Screen("trusted_organizer_apply")
    data object CommunityList : Screen("community_list")
    data object CommunityDetail : Screen("community_detail/{communityId}") {
        fun createRoute(communityId: Int) = "community_detail/$communityId"
    }
    data object CreateCommunity : Screen("create_community?id={id}") {
        fun createRoute(id: Int? = null) = if (id != null) "create_community?id=$id" else "create_community"
    }
    data object CreateEvent : Screen("create_event/{communityId}?eventId={eventId}") {
        fun createRoute(communityId: Int, eventId: Int? = null) = 
            if (eventId != null) "create_event/$communityId?eventId=$eventId" else "create_event/$communityId"
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
fun AppNavigation(
    viewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    val currentUser by viewModel.currentUser.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    val onShowSnackbar: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    // Handle blocking logic
    LaunchedEffect(currentUser) {
        if (currentUser?.isBlocked == true) {
            viewModel.logout()
            navController.navigate(Screen.Home.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val isTopLevel = currentDestination in listOf(Screen.Home.route, Screen.CommunityList.route, Screen.Profile.route)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                        onShowSnackbar("Berhasil masuk")
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
                        onShowSnackbar("Berhasil mendaftar")
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
                    onNavigateToOrganizerRegister = { navController.navigate(Screen.OrganizerRegister.route) },
                    onNavigateToTrustedApply = { navController.navigate(Screen.TrustedOrganizerApply.route) },
                    onNavigateToCommunityDetail = { id -> navController.navigate(Screen.CommunityDetail.createRoute(id)) },
                    onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) },
                    onLogout = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = true }
                        }
                        onShowSnackbar("Berhasil keluar")
                    },
                    onShowSnackbar = onShowSnackbar
                )
            }

            composable(Screen.EditProfile.route) {
                EditProfileScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onShowSnackbar = onShowSnackbar
                )
            }

            composable(Screen.OrganizerRegister.route) {
                OrganizerRegisterScreen(
                    onRegisterSuccess = { _ ->
                        navController.popBackStack()
                        onShowSnackbar("Pendaftaran penyelenggara berhasil")
                    },
                    onNavigateBack = { navController.popBackStack() },
                    onShowSnackbar = onShowSnackbar
                )
            }

            composable(Screen.TrustedOrganizerApply.route) {
                TrustedOrganizerApplyScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onSuccess = { 
                        navController.popBackStack()
                        onShowSnackbar("Permohonan berhasil dikirim")
                    },
                    onShowSnackbar = onShowSnackbar
                )
            }

            composable(Screen.CommunityList.route) {
                CommunityListScreen(
                    currentUser = currentUser,
                    onNavigateToCommunityDetail = { id -> navController.navigate(Screen.CommunityDetail.createRoute(id)) },
                    onNavigateToCreateCommunity = {
                        if ((currentUser?.role == UserRole.ORGANIZER) || (currentUser?.role == UserRole.ADMIN)) {
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
                    currentUser = currentUser,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToForum = { navController.navigate(Screen.Forum.createRoute(communityId)) },
                    onNavigateToCreateEvent = { navController.navigate(Screen.CreateEvent.createRoute(communityId)) },
                    onNavigateToEventDetail = { eventId ->
                        navController.navigate(Screen.EventDetail.createRoute(eventId, communityId))
                    },
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    onNavigateToEditCommunity = { id -> navController.navigate(Screen.CreateCommunity.createRoute(id)) },
                    onShowSnackbar = onShowSnackbar
                )
            }

            composable(
                route = Screen.CreateCommunity.route,
                arguments = listOf(navArgument("id") { 
                    type = NavType.IntType
                    defaultValue = -1 
                })
            ) { backStackEntry ->
                CreateCommunityScreen(
                    onSuccess = { newId ->
                        val id = backStackEntry.arguments?.getInt("id").takeIf { it != -1 }
                        navController.popBackStack()
                        if (id == null) {
                            navController.navigate(Screen.CommunityDetail.createRoute(newId))
                        }
                        onShowSnackbar(if (id == null) "Komunitas berhasil dibuat" else "Komunitas berhasil diperbarui")
                    },
                    onNavigateBack = { navController.popBackStack() },
                    onShowSnackbar = onShowSnackbar
                )
            }

            composable(
                route = Screen.CreateEvent.route,
                arguments = listOf(
                    navArgument("communityId") { type = NavType.IntType },
                    navArgument("eventId") { 
                        type = NavType.IntType
                        defaultValue = -1 
                    }
                )
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getInt("eventId").takeIf { it != -1 }
                CreateEventScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onSuccess = {
                        navController.popBackStack()
                        onShowSnackbar(if (eventId == null) "Acara berhasil dibuat" else "Acara berhasil diperbarui")
                    },
                    onShowSnackbar = onShowSnackbar
                )
            }

            composable(
                route = Screen.EventDetail.route,
                arguments = listOf(
                    navArgument("eventId") { type = NavType.IntType },
                    navArgument("communityId") { type = NavType.IntType }
                )
            ) {
                EventDetailScreen(
                    currentUser = currentUser,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    onNavigateToEditEvent = { eId, cId -> navController.navigate(Screen.CreateEvent.createRoute(cId, eId)) },
                    onShowSnackbar = onShowSnackbar
                )
            }

            composable(
                route = Screen.Forum.route,
                arguments = listOf(navArgument("communityId") { type = NavType.IntType })
            ) {
                ForumScreen(
                    currentUser = currentUser,
                    onNavigateBack = { navController.popBackStack() },
                    onShowSnackbar = onShowSnackbar
                )
            }

            composable(Screen.AdminPanel.route) {
                if (currentUser?.role != UserRole.ADMIN) {
                    LaunchedEffect(Unit) { navController.popBackStack() }
                } else {
                    AdminPanelScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onShowSnackbar = onShowSnackbar
                    )
                }
            }
        }
    }
}

