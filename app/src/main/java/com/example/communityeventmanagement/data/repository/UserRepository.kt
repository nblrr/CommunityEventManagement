package com.example.communityeventmanagement.data.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.communityeventmanagement.data.local.JsonStorage
import com.example.communityeventmanagement.data.model.TrustedApplication
import com.example.communityeventmanagement.data.model.UserProfile

class UserRepository(private val storage: JsonStorage?) {
    var currentUser: UserProfile? by mutableStateOf(null)
    val allUsers = mutableStateListOf<UserProfile>()
    val trustedApplications = mutableStateListOf<TrustedApplication>()

    // Load data user
    fun loadUsers() {
        storage?.let {
            val loadedUsers = it.loadUsers().map { user ->
                UserProfile(
                    id = user.id, name = user.name, email = user.email, password = user.password,
                    avatarUri = user.avatarUri, role = user.role.ifBlank { "User" },
                    isBlocked = user.isBlocked, isTrusted = user.isTrusted,
                    trustedAppStatus = user.trustedAppStatus.ifBlank { "NONE" },
                    organizerProfile = user.organizerProfile
                )
            }
            allUsers.clear(); allUsers.addAll(loadedUsers)
        }
    }

    // Load pengajuan trusted
    fun loadTrustedApplications() {
        storage?.let {
            val loadedApps = it.loadTrustedApplications()
            trustedApplications.clear(); trustedApplications.addAll(loadedApps)
        }
    }

    fun loadSession(): String? = storage?.loadSession()
    fun saveUserData() = storage?.saveUsers(allUsers.toList())
    fun saveTrustedApplications() = storage?.saveTrustedApplications(trustedApplications.toList())
    fun saveSession(userId: String?) = storage?.saveSession(userId)

    // Cek admin
    fun ensureAdminExists() {
        if (!allUsers.any { it.role == "Admin" }) {
            val admin = UserProfile(id = "admin_001", name = "Administrator", email = "admin@app.com", password = "admin123", role = "Admin")
            allUsers.add(admin); saveUserData()
        }
    }

    fun login(user: UserProfile) { currentUser = user; saveSession(user.id) }
    fun logout() { currentUser = null; saveSession(null) }
}
