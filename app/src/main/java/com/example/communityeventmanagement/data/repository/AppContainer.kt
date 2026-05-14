package com.example.communityeventmanagement.data.repository

import android.content.Context
import com.example.communityeventmanagement.data.local.JsonStorage

interface AppContainer {
    val userRepository: UserRepository
    val communityRepository: CommunityRepository
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

    init {
        // Initial data loading
        userRepository.loadUsers()
        communityRepository.loadCommunities()
        userRepository.loadTrustedApplications()
        userRepository.ensureAdminExists()
        
        val savedUserId = userRepository.loadSession()
        if (savedUserId != null) {
            val user = userRepository.allUsers.find { it.id == savedUserId }
            if (user != null && !user.isBlocked) {
                userRepository.currentUser = user
                communityRepository.refreshUserParticipation(user)
            } else if (user?.isBlocked == true) {
                userRepository.saveSession(null)
            }
        }
    }
}
