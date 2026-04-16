package com.example.communityeventmanagement.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.communityeventmanagement.ui.screens.auth.LoginScreen
import com.example.communityeventmanagement.ui.screens.auth.RegisterScreen
import com.example.communityeventmanagement.ui.screens.forum.ForumScreen
import com.example.communityeventmanagement.ui.screens.home.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Menyimpan status login secara sederhana untuk dummy
    var isLoggedIn by remember { mutableStateOf(false) }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                isLoggedIn = isLoggedIn,
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToForum = { navController.navigate("forum") }
            )
        }
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    isLoggedIn = true
                    navController.popBackStack("home", inclusive = false)
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    isLoggedIn = true
                    navController.popBackStack("home", inclusive = false)
                },
                onNavigateToLogin = { navController.navigate("login") { popUpTo("login") { inclusive = true } } }
            )
        }
        composable("forum") {
            ForumScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}