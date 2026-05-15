package com.example.communityeventmanagement.data.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.data.local.JsonStorage
import com.example.communityeventmanagement.data.model.Community
import com.example.communityeventmanagement.data.model.TrustedApplication
import com.example.communityeventmanagement.data.model.UserProfile

class UserRepository(private val storage: JsonStorage) {
    var currentUser: UserProfile? by mutableStateOf(null)
    val users = mutableStateListOf<UserProfile>()
    val trustedApplications = mutableStateListOf<TrustedApplication>()
    var themeMode by mutableStateOf("AUTO") // AUTO, LIGHT, DARK

    // Load user data
    suspend fun loadUsers() {
        val loadedUsers = storage.loadUsers().toMutableList()
        // GSON migration safeguard: ensure required fields are present if loaded from old JSON
        loadedUsers.forEachIndexed { index, user ->
            @Suppress("SENSELESS_COMPARISON")
            if (user.trustedApplicationStatus == null || user.role == null || user.password == null) {
                loadedUsers[index] = user.copy(
                    trustedApplicationStatus = user.trustedApplicationStatus ?: "NONE",
                    role = user.role ?: "User",
                    password = user.password ?: ""
                )
            }
        }
        users.clear()
        users.addAll(loadedUsers)
    }

    // Load trusted applications
    suspend fun loadTrustedApplications() {
        val loadedApps = storage.loadTrustedApplications().map { app ->
            @Suppress("SENSELESS_COMPARISON")
            if (app.status == null) app.copy(status = "PENDING") else app
        }
        trustedApplications.clear()
        trustedApplications.addAll(loadedApps)
    }

    suspend fun loadSession(): String? = storage.loadSession()
    suspend fun loadTheme(): String = storage.loadTheme()
    
    suspend fun saveUserData() = storage.saveUsers(users.toList())
    suspend fun saveTrustedApplications() = storage.saveTrustedApplications(trustedApplications.toList())
    suspend fun saveSession(userId: String?) = storage.saveSession(userId)
    suspend fun saveTheme(mode: String) {
        themeMode = mode
        storage.saveTheme(mode)
    }

    // Ensure admin exists
    suspend fun ensureAdminExists() {
        if (!users.any { it.role == "Admin" }) {
            val admin = UserProfile(id = "admin_001", name = "Administrator", email = "admin@app.com", password = "admin123", role = "Admin")
            users.add(admin)
            saveUserData()
        }
    }

    suspend fun login(user: UserProfile) { 
        currentUser = user
        saveSession(user.id)
    }

    suspend fun logout() { 
        currentUser = null
        saveSession(null)
    }

    suspend fun updateAvatar(newUri: String?) {
        val user = currentUser ?: return
        val index = users.indexOfFirst { it.id == user.id }
        if (index != -1) {
            val updatedUser = users[index].copy(avatarUri = newUri)
            users[index] = updatedUser
            currentUser = updatedUser
            saveUserData()
        }
    }

    suspend fun loginWithCredentials(email: String, password: String): LoginResult {
        val user = users.find { it.email.equals(email.trim(), ignoreCase = true) && it.password == password }
            ?: return LoginResult.ErrorResource(R.string.error_invalid_credentials)
        if (user.isBlocked) return LoginResult.ErrorResource(R.string.error_account_blocked)
        login(user)
        return LoginResult.Success(user)
    }

    suspend fun submitTrustedApplication(communities: List<Community>, reason: String, experience: String) {
        val user = currentUser ?: return
        if (trustedApplications.any { it.userId == user.id }) return
        val communityName = communities.find { it.organizerId == user.id }?.name ?: "Unknown"
        val application = TrustedApplication(user.id, user.name, communityName, reason, experience)
        trustedApplications.add(application)
        saveTrustedApplications()
        val userIndex = users.indexOfFirst { it.id == user.id }
        if (userIndex != -1) {
            users[userIndex] = users[userIndex].copy(trustedApplicationStatus = "PENDING")
            currentUser = users[userIndex]
            saveUserData()
        }
    }

    suspend fun handleTrustedApplication(userId: String, approve: Boolean) {
        val appIndex = trustedApplications.indexOfFirst { it.userId == userId }
        if (appIndex != -1) {
            trustedApplications.removeAt(appIndex)
            saveTrustedApplications()
            val userIndex = users.indexOfFirst { it.id == userId }
            if (userIndex != -1) {
                val status = if (approve) "APPROVED" else "REJECTED"
                users[userIndex] = users[userIndex].copy(trustedApplicationStatus = status, isTrusted = approve)
                if (currentUser?.id == userId) currentUser = users[userIndex]
                saveUserData()
            }
        }
    }
}

sealed class LoginResult {
    data class Success(val user: UserProfile) : LoginResult()
    data class Error(val message: String) : LoginResult()
    data class ErrorResource(val resId: Int) : LoginResult()
}
