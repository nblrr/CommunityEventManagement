package com.example.communityeventmanagement.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.communityeventmanagement.data.model.*
import com.example.communityeventmanagement.data.repository.AppState
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
import com.example.communityeventmanagement.ui.screens.admin.AdminPanelScreen

@Composable
fun AppNavigation() {
    val backStack: SnapshotStateList<Route> = remember {
        mutableStateListOf(Route.Home)
    }
    var currentUser by remember { mutableStateOf(AppState.currentUser) }

    fun updateUser(user: UserProfile?) {
        AppState.currentUser = user
        currentUser = user
    }

    CompositionLocalProvider(LocalBackStack provides backStack) {
        NavDisplay(
            backStack = backStack,
            currentUser = currentUser,
            onUpdateUser = ::updateUser
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun NavDisplay(
    backStack: SnapshotStateList<Route>,
    currentUser: UserProfile?,
    onUpdateUser: (UserProfile?) -> Unit
) {
    val currentRoute = backStack.lastOrNull() ?: Route.Home

    AnimatedContent(
        targetState = currentRoute,
        transitionSpec = {
            (slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))) togetherWith
                    (slideOutHorizontally(
                        targetOffsetX = { -it / 3 },
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(150)))
        },
        label = "NavTransition"
    ) { route ->
        when (route) {
            Route.Home -> HomeScreen(
                currentUser = currentUser,
                onNavigateToLogin = { backStack.add(Route.Login) },
                onNavigateToProfile = {
                    if (currentUser != null) backStack.add(Route.Profile)
                    else backStack.add(Route.Login)
                },
                onNavigateToCommunityList = { backStack.add(Route.CommunityList) },
                onNavigateToAdminPanel = { backStack.add(Route.AdminPanel) },
                onNavigateToCommunityDetail = { id -> backStack.add(Route.CommunityDetail(id)) },
                onNavigateToEventDetail = { eventId, commId -> backStack.add(Route.EventDetail(eventId, commId)) }
            )

            Route.Login -> LoginScreen(
                onLoginSuccess = { user ->
                    onUpdateUser(user)
                    while (backStack.size > 1) backStack.removeLastOrNull()
                },
                onNavigateToRegister = {
                    backStack.removeLastOrNull()
                    backStack.add(Route.Register)
                },
                onNavigateBack = { backStack.removeLastOrNull() }
            )

            Route.Register -> RegisterScreen(
                onRegisterSuccess = { user ->
                    onUpdateUser(user)
                    while (backStack.size > 1) backStack.removeLastOrNull()
                },
                onNavigateToLogin = {
                    backStack.removeLastOrNull()
                    backStack.add(Route.Login)
                },
                onNavigateBack = { backStack.removeLastOrNull() }
            )

            Route.Profile -> ProfileScreen(
                currentUser = currentUser,
                onNavigateBack = { backStack.removeLastOrNull() },
                onNavigateToOrganizerRegister = { backStack.add(Route.OrganizerRegister) },
                onNavigateToCommunityDetail = { communityId -> backStack.add(Route.CommunityDetail(communityId)) },
                onLogout = {
                    onUpdateUser(null)
                    AppState.joinedCommunityIds.clear()
                    AppState.registeredEventIds.clear()
                    while (backStack.size > 1) backStack.removeLastOrNull()
                }
            )

            Route.OrganizerRegister -> OrganizerRegisterScreen(
                currentUser = currentUser,
                onRegisterSuccess = { updatedUser ->
                    onUpdateUser(updatedUser)
                    backStack.removeLastOrNull()
                },
                onNavigateBack = { backStack.removeLastOrNull() }
            )

            Route.CommunityList -> CommunityListScreen(
                currentUser = currentUser,
                onNavigateBack = { backStack.removeLastOrNull() },
                onNavigateToCommunityDetail = { communityId ->
                    backStack.add(Route.CommunityDetail(communityId))
                },
                onNavigateToCreateCommunity = {
                    if (currentUser?.role == "Organizer" || currentUser?.role == "Admin") backStack.add(Route.CreateCommunity)
                    else backStack.add(Route.Login)
                }
            )

            is Route.CommunityDetail -> CommunityDetailScreen(
                communityId = route.communityId,
                currentUser = currentUser,
                onNavigateBack = { backStack.removeLastOrNull() },
                onNavigateToForum = { backStack.add(Route.Forum(route.communityId)) },
                onNavigateToCreateEvent = { backStack.add(Route.CreateEvent(route.communityId)) },
                onNavigateToEventDetail = { eventId ->
                    backStack.add(Route.EventDetail(eventId, route.communityId))
                },
                onNavigateToLogin = { backStack.add(Route.Login) }
            )

            Route.CreateCommunity -> CreateCommunityScreen(
                currentUser = currentUser,
                onCreateSuccess = { newCommunityId ->
                    backStack.removeLastOrNull()
                    backStack.add(Route.CommunityDetail(newCommunityId))
                },
                onNavigateBack = { backStack.removeLastOrNull() }
            )

            is Route.CreateEvent -> CreateEventScreen(
                communityId = route.communityId,
                onCreateSuccess = { backStack.removeLastOrNull() },
                onNavigateBack = { backStack.removeLastOrNull() }
            )

            is Route.EventDetail -> EventDetailScreen(
                eventId = route.eventId,
                communityId = route.communityId,
                currentUser = currentUser,
                onNavigateBack = { backStack.removeLastOrNull() },
                onNavigateToLogin = { backStack.add(Route.Login) }
            )

            is Route.Forum -> ForumScreen(
                communityId = route.communityId,
                currentUser = currentUser,
                onNavigateBack = { backStack.removeLastOrNull() }
            )

            Route.AdminPanel -> AdminPanelScreen(
                onNavigateBack = { backStack.removeLastOrNull() }
            )
        }
    }
}
