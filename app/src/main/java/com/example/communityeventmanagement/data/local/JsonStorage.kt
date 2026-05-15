package com.example.communityeventmanagement.data.local

import android.content.Context
import com.example.communityeventmanagement.data.model.Community
import com.example.communityeventmanagement.data.model.ForumMessage
import com.example.communityeventmanagement.data.model.TrustedApplication
import com.example.communityeventmanagement.data.model.UserProfile
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class JsonStorage(context: Context, manualFilesDir: File? = null) {
    private val appContext = context.applicationContext
    private val gson = Gson()
    private val filesDir: File = manualFilesDir ?: context.filesDir
    private val usersFile = File(filesDir, "users.json")
    private val communitiesFile = File(filesDir, "communities.json")
    private val sessionFile = File(filesDir, "session.json")
    private val trustedAppsFile = File(filesDir, "trusted_applications.json")
    private val themeFile = File(filesDir, "theme.json")

    private fun loadFromAssets(fileName: String): String? {
        return try {
            appContext.assets.open("data/$fileName").bufferedReader().use { it.readText() }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun saveSession(userId: String?) = withContext(Dispatchers.IO) {
        sessionFile.writeText(userId ?: "")
    }

    suspend fun loadSession(): String? = withContext(Dispatchers.IO) {
        if (!sessionFile.exists()) return@withContext null
        val id = sessionFile.readText().trim()
        id.ifEmpty { null }
    }

    suspend fun saveUsers(users: List<UserProfile>) = withContext(Dispatchers.IO) {
        usersFile.writeText(gson.toJson(users))
    }

    suspend fun loadUsers(): List<UserProfile> = withContext(Dispatchers.IO) {
        if (!usersFile.exists()) {
            loadFromAssets("users.json")?.let { assetData ->
                return@withContext try {
                    gson.fromJson(assetData, object : TypeToken<List<UserProfile>>() {}.type) ?: emptyList()
                } catch (_: Exception) {
                    emptyList()
                }
            }
            return@withContext emptyList()
        }
        try {
            gson.fromJson<List<UserProfile>>(
                usersFile.readText(),
                object : TypeToken<List<UserProfile>>() {}.type
            ) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun saveCommunities(communities: List<Community>) = withContext(Dispatchers.IO) {
        communitiesFile.writeText(gson.toJson(communities))
    }

    suspend fun loadCommunities(): List<Community> = withContext(Dispatchers.IO) {
        if (!communitiesFile.exists()) {
            loadFromAssets("communities.json")?.let { assetData ->
                return@withContext try {
                    gson.fromJson(assetData, object : TypeToken<List<Community>>() {}.type) ?: emptyList()
                } catch (_: Exception) {
                    emptyList()
                }
            }
            return@withContext emptyList()
        }
        try {
            gson.fromJson<List<Community>>(
                communitiesFile.readText(),
                object : TypeToken<List<Community>>() {}.type
            ) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun saveForumMessages(communityId: Int, messages: List<ForumMessage>) = withContext(Dispatchers.IO) {
        val file = File(filesDir, "forum_$communityId.json")
        file.writeText(gson.toJson(messages))
    }

    suspend fun loadForumMessages(communityId: Int): List<ForumMessage> = withContext(Dispatchers.IO) {
        val file = File(filesDir, "forum_$communityId.json")
        if (!file.exists()) return@withContext emptyList()
        try {
            gson.fromJson<List<ForumMessage>>(
                file.readText(),
                object : TypeToken<List<ForumMessage>>() {}.type
            ) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun saveTrustedApplications(apps: List<TrustedApplication>) = withContext(Dispatchers.IO) {
        trustedAppsFile.writeText(gson.toJson(apps))
    }

    suspend fun loadTrustedApplications(): List<TrustedApplication> = withContext(Dispatchers.IO) {
        if (!trustedAppsFile.exists()) return@withContext emptyList()
        try {
            gson.fromJson<List<TrustedApplication>>(
                trustedAppsFile.readText(),
                object : TypeToken<List<TrustedApplication>>() {}.type
            ) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun saveTheme(mode: String) = withContext(Dispatchers.IO) {
        themeFile.writeText(mode)
    }

    suspend fun loadTheme(): String = withContext(Dispatchers.IO) {
        if (!themeFile.exists()) return@withContext "AUTO"
        themeFile.readText().trim().ifEmpty { "AUTO" }
    }
}
