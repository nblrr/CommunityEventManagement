package com.example.communityeventmanagement.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
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
    val backStack: SnapshotStateList<Route> = remember {
        mutableStateListOf(Route.Home)
    }

    var currentUser by remember { mutableStateOf<UserProfile?>(AppState.currentUser) }

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
            // Layar baru slide masuk dari kanan, layar lama slide keluar ke kiri
            (slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))) togetherWith
                    (slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth / 3 },
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(150)))
        },
        label = "NavTransition"
    ) { route ->

        when (route) {
            // HOME
            Route.Home -> {
                HomeScreen(
                    currentUser = currentUser,
                    onNavigateToLogin = {
                        backStack.add(Route.Login)
                    },
                    onNavigateToProfile = {
                        // Conditional Navigation
                        if (currentUser != null) {
                            backStack.add(Route.Profile)
                        } else {
                            backStack.add(Route.Login)
                        }
                    },
                    onNavigateToCommunityList = {
                        backStack.add(Route.CommunityList)
                    }
                )
            }

            // AUTH: LOGIN
            Route.Login -> {
                LoginScreen(
                    onLoginSuccess = { user ->
                        onUpdateUser(user)
                        while (backStack.size > 1) backStack.removeLastOrNull()
                    },
                    onNavigateToRegister = {
                        backStack.removeLastOrNull()
                        backStack.add(Route.Register)
                    }
                )
            }

            // AUTH: REGISTER
            Route.Register -> {
                RegisterScreen(
                    onRegisterSuccess = { user ->
                        onUpdateUser(user)
                        while (backStack.size > 1) backStack.removeLastOrNull()
                    },
                    onNavigateToLogin = {
                        backStack.removeLastOrNull()
                        backStack.add(Route.Login)
                    }
                )
            }

            // PROFILE
            Route.Profile -> {
                ProfileScreen(
                    currentUser = currentUser,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                    onNavigateToOrganizerRegister = {
                        backStack.add(Route.OrganizerRegister)
                    },
                    onLogout = {
                        onUpdateUser(null)
                        AppState.joinedCommunityIds.clear()
                        // Setelah logout, kembali ke Home
                        while (backStack.size > 1) backStack.removeLastOrNull()
                    }
                )
            }

            // ORGANIZER REGISTER
            Route.OrganizerRegister -> {
                OrganizerRegisterScreen(
                    currentUser = currentUser,
                    onRegisterSuccess = { updatedUser ->
                        onUpdateUser(updatedUser)
                        // Kembali ke Profile setelah berhasil daftar organizer
                        backStack.removeLastOrNull()
                    },
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            // COMMUNITY LIST
            Route.CommunityList -> {
                CommunityListScreen(
                    currentUser = currentUser,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                    onNavigateToCommunityDetail = { communityId ->
                        backStack.add(Route.CommunityDetail(communityId))
                    },
                    onNavigateToCreateCommunity = {
                        if (currentUser?.isOrganizer == true) {
                            backStack.add(Route.CreateCommunity)
                        } else {
                            backStack.add(Route.Login)
                        }
                    }
                )
            }

            // COMMUNITY DETAIL
            is Route.CommunityDetail -> {
                CommunityDetailScreen(
                    communityId = route.communityId,
                    currentUser = currentUser,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                    onNavigateToForum = {
                        backStack.add(Route.Forum(route.communityId))
                    },
                    onNavigateToCreateEvent = {
                        backStack.add(Route.CreateEvent(route.communityId))
                    },
                    onNavigateToLogin = {
                        backStack.add(Route.Login)
                    }
                )
            }

            // CREATE COMMUNITY
            Route.CreateCommunity -> {
                CreateCommunityScreen(
                    currentUser = currentUser,
                    onCreateSuccess = { newCommunityId ->
                        backStack.removeLastOrNull()
                        backStack.add(Route.CommunityDetail(newCommunityId))
                    },
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            // CREATE EVENT
            is Route.CreateEvent -> {
                CreateEventScreen(
                    communityId = route.communityId,
                    currentUser = currentUser,
                    onCreateSuccess = {
                        backStack.removeLastOrNull()   // Back Navigation ke Community Detail
                    },
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            // FORUM
            is Route.Forum -> {
                ForumScreen(
                    communityId = route.communityId,
                    currentUser = currentUser,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        }
    }
}