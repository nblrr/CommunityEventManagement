package com.example.communityeventmanagement.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.example.communityeventmanagement.data.model.UserProfile
import com.example.communityeventmanagement.data.repository.AppState
import com.example.communityeventmanagement.ui.components.AppBottomBar
import com.example.communityeventmanagement.ui.screens.admin.AdminPanelScreen
import com.example.communityeventmanagement.ui.screens.auth.LoginScreen
import com.example.communityeventmanagement.ui.screens.auth.RegisterScreen
import com.example.communityeventmanagement.ui.screens.community.CommunityDetailScreen
import com.example.communityeventmanagement.ui.screens.community.CommunityListScreen
import com.example.communityeventmanagement.ui.screens.community.CreateCommunityScreen
import com.example.communityeventmanagement.ui.screens.event.CreateEventScreen
import com.example.communityeventmanagement.ui.screens.event.EventDetailScreen
import com.example.communityeventmanagement.ui.screens.forum.ForumScreen
import com.example.communityeventmanagement.ui.screens.home.HomeScreen
import com.example.communityeventmanagement.ui.screens.organizer.OrganizerRegisterScreen
import com.example.communityeventmanagement.ui.screens.profile.ProfileScreen

// Route navigasi
sealed class Route {
    data object Login : Route()
    data object Register : Route()
    data object Home : Route()
    data object Profile : Route()
    data object OrganizerRegister : Route()
    data object CommunityList : Route()
    data class CommunityDetail(val communityId: Int) : Route()
    data object CreateCommunity : Route()
    data class CreateEvent(val communityId: Int) : Route()
    data class EventDetail(val eventId: Int, val communityId: Int) : Route()
    data class Forum(val communityId: Int) : Route()
    data object AdminPanel : Route()
}

// CompositionLocal untuk BackStack
val LocalBackStack = compositionLocalOf<SnapshotStateList<Route>> {
    error("LocalBackStack not found!")
}

// Komponen Navigasi Utama
@Composable
fun AppNavigation() {
    val backStack: SnapshotStateList<Route> = remember {
        mutableStateListOf(Route.Home)
    }
    val currentUser = AppState.currentUser

    // Monitor if current user gets blocked
    LaunchedEffect(currentUser?.isBlocked) {
        if (currentUser?.isBlocked == true) {
            AppState.logout()
            while (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
            if (backStack.firstOrNull() != Route.Home) {
                backStack.clear(); backStack.add(Route.Home)
            }
        }
    }

    CompositionLocalProvider(LocalBackStack provides backStack) {
        BackHandler(enabled = backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }

        NavDisplay(
            backStack = backStack,
            currentUser = currentUser,
            onUpdateUser = { AppState.currentUser = it }
        )
    }
}

// Tampilan Navigasi
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun NavDisplay(
    backStack: SnapshotStateList<Route>,
    currentUser: UserProfile?,
    onUpdateUser: (UserProfile?) -> Unit
) {
    val currentRoute = backStack.lastOrNull() ?: Route.Home
    val isTopLevel = currentRoute is Route.Home || currentRoute is Route.CommunityList || currentRoute is Route.Profile

    val navigateToHome: () -> Unit = {
        while (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
        if (backStack.firstOrNull() != Route.Home) {
            backStack.clear(); backStack.add(Route.Home)
        }
    }

    val navigateToCommunities: () -> Unit = {
        if (currentRoute !is Route.CommunityList) {
            if (backStack.contains(Route.CommunityList)) {
                while (backStack.last() != Route.CommunityList) backStack.removeAt(backStack.lastIndex)
            } else {
                backStack.add(Route.CommunityList)
            }
        }
    }

    val navigateToProfile: () -> Unit = {
        if (currentUser != null) {
            if (currentRoute !is Route.Profile) {
                if (backStack.contains(Route.Profile)) {
                    while (backStack.last() != Route.Profile) backStack.removeAt(backStack.lastIndex)
                } else {
                    backStack.add(Route.Profile)
                }
            }
        } else {
            backStack.add(Route.Login)
        }
    }

    Scaffold(
        bottomBar = {
            if (isTopLevel) {
                AppBottomBar(
                    currentUser = currentUser,
                    currentRoute = when (currentRoute) {
                        Route.Home -> "home"
                        Route.CommunityList -> "communities"
                        Route.Profile -> "profile"
                        else -> ""
                    },
                    onNavigateToHome = navigateToHome,
                    onNavigateToCommunities = navigateToCommunities,
                    onNavigateToProfile = navigateToProfile
                )
            }
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = currentRoute,
            modifier = Modifier.padding(paddingValues),
            transitionSpec = {
                (slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) + fadeIn(animationSpec = tween(300))) togetherWith
                (slideOutHorizontally(targetOffsetX = { -it / 3 }, animationSpec = tween(300)) + fadeOut(animationSpec = tween(150)))
            },
            label = "NavTransition"
        ) { route ->
            when (route) {
                Route.Home -> HomeScreen(
                    currentUser = currentUser,
                    onNavigateToCommunityList = navigateToCommunities,
                    onNavigateToAdminPanel = { if (currentUser?.role == "Admin") backStack.add(Route.AdminPanel) },
                    onNavigateToCommunityDetail = { id -> backStack.add(Route.CommunityDetail(id)) },
                    onNavigateToEventDetail = { eventId, commId -> backStack.add(Route.EventDetail(eventId, commId)) }
                )

                Route.Login -> LoginScreen(
                    onLoginSuccess = { user -> onUpdateUser(user); while (backStack.size > 1) backStack.removeAt(backStack.lastIndex) },
                    onNavigateToRegister = { backStack.removeAt(backStack.lastIndex); backStack.add(Route.Register) },
                    onNavigateBack = { backStack.removeAt(backStack.lastIndex) }
                )

                Route.Register -> RegisterScreen(
                    onRegisterSuccess = { user -> AppState.login(user); onUpdateUser(user); while (backStack.size > 1) backStack.removeAt(backStack.lastIndex) },
                    onNavigateToLogin = { backStack.removeAt(backStack.lastIndex); backStack.add(Route.Login) },
                    onNavigateBack = { backStack.removeAt(backStack.lastIndex) }
                )

                Route.Profile -> ProfileScreen(
                    currentUser = currentUser,
                    onNavigateToOrganizerRegister = { backStack.add(Route.OrganizerRegister) },
                    onNavigateToCommunityDetail = { communityId -> backStack.add(Route.CommunityDetail(communityId)) },
                    onLogout = { AppState.logout(); onUpdateUser(null); while (backStack.size > 1) backStack.removeAt(backStack.lastIndex) }
                )

                Route.OrganizerRegister -> OrganizerRegisterScreen(
                    currentUser = currentUser,
                    onRegisterSuccess = { updatedUser -> AppState.login(updatedUser); onUpdateUser(updatedUser); backStack.removeAt(backStack.lastIndex) },
                    onNavigateBack = { backStack.removeAt(backStack.lastIndex) }
                )

                Route.CommunityList -> CommunityListScreen(
                    currentUser = currentUser,
                    onNavigateToCommunityDetail = { communityId -> backStack.add(Route.CommunityDetail(communityId)) },
                    onNavigateToCreateCommunity = {
                        if (currentUser?.role == "Organizer" || currentUser?.role == "Admin") backStack.add(Route.CreateCommunity)
                        else backStack.add(Route.Login)
                    }
                )

                is Route.CommunityDetail -> CommunityDetailScreen(
                    communityId = route.communityId,
                    currentUser = currentUser,
                    onNavigateBack = { backStack.removeAt(backStack.lastIndex) },
                    onNavigateToForum = { backStack.add(Route.Forum(route.communityId)) },
                    onNavigateToCreateEvent = { backStack.add(Route.CreateEvent(route.communityId)) },
                    onNavigateToEventDetail = { eventId -> backStack.add(Route.EventDetail(eventId, route.communityId)) },
                    onNavigateToLogin = { backStack.add(Route.Login) }
                )

                Route.CreateCommunity -> CreateCommunityScreen(
                    currentUser = currentUser,
                    onCreateSuccess = { newCommunityId -> backStack.removeAt(backStack.lastIndex); backStack.add(Route.CommunityDetail(newCommunityId)) },
                    onNavigateBack = { backStack.removeAt(backStack.lastIndex) }
                )

                is Route.CreateEvent -> CreateEventScreen(
                    communityId = route.communityId,
                    onCreateSuccess = { backStack.removeAt(backStack.lastIndex) },
                    onNavigateBack = { backStack.removeAt(backStack.lastIndex) }
                )

                is Route.EventDetail -> EventDetailScreen(
                    eventId = route.eventId,
                    communityId = route.communityId,
                    currentUser = currentUser,
                    onNavigateBack = { backStack.removeAt(backStack.lastIndex) },
                    onNavigateToLogin = { backStack.add(Route.Login) }
                )

                is Route.Forum -> ForumScreen(
                    communityId = route.communityId,
                    currentUser = currentUser,
                    onNavigateBack = { backStack.removeAt(backStack.lastIndex) }
                )

                Route.AdminPanel -> {
                    if (currentUser?.role != "Admin") {
                        LaunchedEffect(Unit) { backStack.removeAt(backStack.lastIndex) }
                    } else {
                        AdminPanelScreen(onNavigateBack = { backStack.removeAt(backStack.lastIndex) })
                    }
                }
            }
        }
    }
}
