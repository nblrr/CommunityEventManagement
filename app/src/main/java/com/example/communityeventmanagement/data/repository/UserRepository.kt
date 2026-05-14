package com.example.communityeventmanagement.data.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.communityeventmanagement.data.local.JsonStorage
import com.example.communityeventmanagement.data.model.Community
import com.example.communityeventmanagement.data.model.TrustedApplication
import com.example.communityeventmanagement.data.model.UserProfile

class UserRepository(private val storage: JsonStorage?) {
    var currentUser: UserProfile? by mutableStateOf(null)
    val allUsers = mutableStateListOf<UserProfile>()
    val trustedApplications = mutableStateListOf<TrustedApplication>()

    // Load data user
    fun loadUsers() {
        storage?.let {
            val loadedUsers = it.loadUsers()
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

    fun login(user: UserProfile) { 
        currentUser = user
        saveSession(user.id)
    }

    fun logout() { 
        currentUser = null
        saveSession(null)
    }

    fun updateAvatar(newUri: String?) {
        val user = currentUser ?: return
        val index = allUsers.indexOfFirst { it.id == user.id }
        if (index != -1) {
            val updatedUser = allUsers[index].copy(avatarUri = newUri)
            allUsers[index] = updatedUser
            currentUser = updatedUser
            saveUserData()
        }
    }

    fun loginWithCredentials(email: String, password: String): LoginResult {
        val user = allUsers.find { it.email.equals(email.trim(), ignoreCase = true) && it.password == password }
            ?: return LoginResult.Error("Email atau password salah.")
        if (user.isBlocked) return LoginResult.Error("Akun ini telah diblokir oleh admin.")
        login(user)
        return LoginResult.Success(user)
    }

    fun submitTrustedApplication(communities: List<Community>, reason: String, experience: String) {
        val user = currentUser ?: return
        if (trustedApplications.any { it.userId == user.id }) return
        val communityName = communities.find { it.organizerId == user.id }?.name ?: "Unknown"
        val application = TrustedApplication(user.id, user.name, communityName, reason, experience)
        trustedApplications.add(application)
        saveTrustedApplications()
        val userIndex = allUsers.indexOfFirst { it.id == user.id }
        if (userIndex != -1) {
            allUsers[userIndex] = allUsers[userIndex].copy(trustedAppStatus = "PENDING")
            currentUser = allUsers[userIndex]
            saveUserData()
        }
    }

    fun handleTrustedApplication(userId: String, approve: Boolean) {
        val appIndex = trustedApplications.indexOfFirst { it.userId == userId }
        if (appIndex != -1) {
            trustedApplications.removeAt(appIndex)
            saveTrustedApplications()
            val userIndex = allUsers.indexOfFirst { it.id == userId }
            if (userIndex != -1) {
                val status = if (approve) "APPROVED" else "REJECTED"
                allUsers[userIndex] = allUsers[userIndex].copy(trustedAppStatus = status, isTrusted = approve)
                if (currentUser?.id == userId) currentUser = allUsers[userIndex]
                saveUserData()
            }
        }
    }
}

sealed class LoginResult {
    data class Success(val user: UserProfile) : LoginResult()
    data class Error(val message: String) : LoginResult()
}
