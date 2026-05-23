package com.example.communityeventmanagement.data.repository

import com.example.communityeventmanagement.data.mapper.toDomain
import com.example.communityeventmanagement.data.mapper.toDto
import com.example.communityeventmanagement.data.source.local.DataStoreManager
import com.example.communityeventmanagement.data.source.local.JsonDataSource
import com.example.communityeventmanagement.domain.entities.ApplicationStatus
import com.example.communityeventmanagement.domain.entities.Community
import com.example.communityeventmanagement.domain.entities.Organizer
import com.example.communityeventmanagement.domain.entities.ThemeMode
import com.example.communityeventmanagement.domain.entities.TrustedApplication
import com.example.communityeventmanagement.domain.entities.User
import com.example.communityeventmanagement.domain.entities.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import com.example.communityeventmanagement.domain.repository.UserRepository as IUserRepository

class UserRepository(
    private val dataSource: JsonDataSource,
    private val dataStoreManager: DataStoreManager
) : IUserRepository {
    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    override val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _trustedApplications = MutableStateFlow<List<TrustedApplication>>(emptyList())
    override val trustedApplications: StateFlow<List<TrustedApplication>> = _trustedApplications.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.AUTO)
    override val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    override suspend fun initialize() {
        val loadedUsers = dataSource.loadUsers().map { it.toDomain() }
        _users.value = loadedUsers
        _trustedApplications.value = dataSource.loadTrustedApplications().map { it.toDomain() }
        
        _themeMode.value = try {
            val savedTheme = dataStoreManager.themeMode.first()
            if (savedTheme != null) ThemeMode.valueOf(savedTheme) else ThemeMode.AUTO
        } catch (_: Exception) {
            ThemeMode.AUTO
        }
        
        val savedUserId = dataStoreManager.sessionUserId.first()
        if (savedUserId != null) {
            val user = _users.value.find { it.id == savedUserId }
            if (user != null && !user.isBlocked) {
                _currentUser.value = user
            } else if (user?.isBlocked == true) {
                dataStoreManager.saveSession(null)
            }
        }
    }

    override suspend fun login(user: User) {
        _currentUser.value = user
        dataStoreManager.saveSession(user.id)
    }

    override suspend fun logout() {
        _currentUser.value = null
        dataStoreManager.saveSession(null)
    }

    override suspend fun updateAvatar(newUri: String?): Result<Unit> {
        return try {
            val user = _currentUser.value ?: return Result.failure(Exception("NOT_LOGGED_IN"))
            val updatedUsers = _users.value.map {
                if (it.id == user.id) it.copy(avatarUri = newUri) else it
            }
            _users.value = updatedUsers
            _currentUser.value = updatedUsers.find { it.id == user.id }
            saveUsers()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(name: String, bio: String): Result<Unit> {
        val user = _currentUser.value ?: return Result.failure(Exception("NOT_LOGGED_IN"))
        val updatedUsers = _users.value.map {
            if (it.id == user.id) it.copy(name = name, bio = bio) else it
        }
        _users.value = updatedUsers
        _currentUser.value = updatedUsers.find { it.id == user.id }
        saveUsers()
        return Result.success(Unit)
    }

    override suspend fun loginWithCredentials(email: String, password: String): Result<User> {
        val trimmedEmail = email.trim()
        val user = _users.value.find { it.email.equals(trimmedEmail, ignoreCase = true) && it.password == password }
            ?: return Result.failure(Exception("INVALID_CREDENTIALS"))
        if (user.isBlocked) return Result.failure(Exception("ACCOUNT_BLOCKED"))
        login(user)
        return Result.success(user)
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        val trimmedEmail = email.trim()
        if (_users.value.any { it.email.equals(trimmedEmail, ignoreCase = true) }) {
            return Result.failure(Exception("EMAIL_ALREADY_REGISTERED"))
        }
        val newUser = User(
            id = "user_${System.currentTimeMillis()}",
            name = name.trim(),
            email = trimmedEmail,
            password = password,
        )
        _users.value += newUser
        saveUsers()
        login(newUser)
        return Result.success(newUser)
    }

    override suspend fun submitTrustedApplication(communities: List<Community>, reason: String, experience: String): Result<Unit> {
        return try {
            val user = _currentUser.value ?: return Result.failure(Exception("NOT_LOGGED_IN"))
            if (_trustedApplications.value.any { it.userId == user.id }) return Result.success(Unit)
            val communityName = communities.find { it.organizerId == user.id }?.name ?: "Unknown"
            val application = TrustedApplication(user.id, user.name, communityName, reason, experience, ApplicationStatus.PENDING)
            _trustedApplications.value = _trustedApplications.value + application
            saveTrustedApplications()
            
            val updatedUsers = _users.value.map {
                if (it.id == user.id) it.copy(trustedApplicationStatus = ApplicationStatus.PENDING) else it
            }
            _users.value = updatedUsers
            _currentUser.value = updatedUsers.find { it.id == user.id }
            saveUsers()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun handleTrustedApplication(userId: String, approve: Boolean): Result<Unit> {
        return try {
            val updatedApps = _trustedApplications.value.filter { it.userId != userId }
            _trustedApplications.value = updatedApps
            saveTrustedApplications()

            val updatedUsers = _users.value.map {
                if (it.id == userId) {
                    val status = if (approve) ApplicationStatus.APPROVED else ApplicationStatus.REJECTED
                    it.copy(trustedApplicationStatus = status, isTrusted = approve)
                } else it
            }
            _users.value = updatedUsers
            if (_currentUser.value?.id == userId) {
                _currentUser.value = updatedUsers.find { it.id == userId }
            }
            saveUsers()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleUserBlock(userId: String): Result<Unit> {
        return try {
            val updatedUsers = _users.value.map {
                if (it.id == userId) it.copy(isBlocked = !it.isBlocked) else it
            }
            _users.value = updatedUsers
            
            val updatedTargetUser = updatedUsers.find { it.id == userId }
            if (_currentUser.value?.id == userId) {
                if (updatedTargetUser?.isBlocked == true) {
                    _currentUser.value = null
                    dataStoreManager.saveSession(null)
                } else {
                    _currentUser.value = updatedTargetUser
                }
            }
            saveUsers()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registerOrganizer(userId: String, organizer: Organizer): Result<Unit> {
        return try {
            val updatedUsers = _users.value.map {
                if (it.id == userId) {
                    it.copy(role = UserRole.ORGANIZER, organizer = organizer)
                } else it
            }
            _users.value = updatedUsers
            if (_currentUser.value?.id == userId) {
                _currentUser.value = updatedUsers.find { it.id == userId }
            }
            saveUsers()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveTheme(mode: ThemeMode) {
        _themeMode.value = mode
        dataStoreManager.saveTheme(mode.name)
    }

    private fun saveUsers() {
        dataSource.saveUsers(_users.value.map { it.toDto() })
    }

    private fun saveTrustedApplications() {
        dataSource.saveTrustedApplications(_trustedApplications.value.map { it.toDto() })
    }
}
