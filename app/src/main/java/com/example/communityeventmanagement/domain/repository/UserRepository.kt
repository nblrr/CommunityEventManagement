package com.example.communityeventmanagement.domain.repository

import com.example.communityeventmanagement.domain.entities.Community
import com.example.communityeventmanagement.domain.entities.Organizer
import com.example.communityeventmanagement.domain.entities.ThemeMode
import com.example.communityeventmanagement.domain.entities.TrustedApplication
import com.example.communityeventmanagement.domain.entities.User
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val currentUser: StateFlow<User?>
    val users: StateFlow<List<User>>
    val trustedApplications: StateFlow<List<TrustedApplication>>
    val themeMode: StateFlow<ThemeMode>

    suspend fun login(user: User)
    suspend fun logout()
    suspend fun updateAvatar(newUri: String?): Result<Unit>
    suspend fun updateProfile(name: String, bio: String): Result<Unit>
    suspend fun loginWithCredentials(email: String, password: String): Result<User>
    suspend fun register(name: String, email: String, password: String): Result<User>
    suspend fun submitTrustedApplication(communities: List<Community>, reason: String, experience: String): Result<Unit>
    suspend fun handleTrustedApplication(userId: String, approve: Boolean): Result<Unit>
    suspend fun toggleUserBlock(userId: String): Result<Unit>
    suspend fun registerOrganizer(userId: String, organizer: Organizer): Result<Unit>
    suspend fun saveTheme(mode: ThemeMode)
    suspend fun initialize()
}
