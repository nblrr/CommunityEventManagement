package com.example.communityeventmanagement.domain.repository

import com.example.communityeventmanagement.domain.model.Organizer
import com.example.communityeventmanagement.domain.model.ThemeMode
import com.example.communityeventmanagement.domain.model.TrustedApplication
import com.example.communityeventmanagement.domain.model.User
import com.example.communityeventmanagement.util.Resource
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val currentUser: StateFlow<User?>
    val users: StateFlow<List<User>>
    val trustedApplications: StateFlow<List<TrustedApplication>>
    val themeMode: StateFlow<ThemeMode>

    suspend fun login(user: User)
    suspend fun logout()
    suspend fun updateAvatar(newUri: String?): Resource<Unit>
    suspend fun updateProfile(name: String, bio: String): Resource<Unit>
    suspend fun loginWithCredentials(email: String, password: String): Resource<User>
    suspend fun register(name: String, email: String, password: String): Resource<User>
    suspend fun addAdmin(name: String, email: String, password: String): Resource<User>
    suspend fun submitTrustedApplication(communityName: String, reason: String, experience: String): Resource<Unit>
    suspend fun handleTrustedApplication(userId: String, approve: Boolean): Resource<Unit>
    suspend fun toggleUserBlock(userId: String): Resource<Unit>
    suspend fun registerOrganizer(userId: String, organizer: Organizer): Resource<Unit>
    suspend fun saveTheme(mode: ThemeMode)
    suspend fun initialize()
}

