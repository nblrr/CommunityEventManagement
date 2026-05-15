package com.example.communityeventmanagement.data.repository

import android.content.Context
import com.example.communityeventmanagement.data.local.JsonStorage

interface AppContainer {
    val userRepository: UserRepository
    val communityRepository: CommunityRepository
    suspend fun initialize()
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val storage: JsonStorage by lazy {
        JsonStorage(context.applicationContext)
    }

    override val userRepository: UserRepository by lazy {
        UserRepository(storage)
    }

    override val communityRepository: CommunityRepository by lazy {
        CommunityRepository(storage)
    }

    override suspend fun initialize() {
        // Initial data loading
        userRepository.loadUsers()
        communityRepository.loadCommunities()
        userRepository.loadTrustedApplications()
        userRepository.ensureAdminExists()
        userRepository.themeMode = userRepository.loadTheme()
        
        val savedUserId = userRepository.loadSession()
        if (savedUserId != null) {
            val user = userRepository.users.find { it.id == savedUserId }
            if (user != null && !user.isBlocked) {
                userRepository.currentUser = user
                communityRepository.refreshUserParticipation(user)
            } else if (user?.isBlocked == true) {
                userRepository.saveSession(null)
            }
        }
    }
}
