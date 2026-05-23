package com.example.communityeventmanagement.data.source.local

import android.content.Context
import com.example.communityeventmanagement.data.dto.CommunityDto
import com.example.communityeventmanagement.data.dto.ForumMessageDto
import com.example.communityeventmanagement.data.dto.TrustedApplicationDto
import com.example.communityeventmanagement.data.dto.UserDto
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File

class JsonDataSource(private val appContext: Context, private val baseDir: File? = null) {
    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create()
    private val filesDir = baseDir ?: appContext.filesDir
    
    private val usersFile = File(filesDir, "users.json")
    private val communitiesFile = File(filesDir, "communities.json")
    private val trustedAppsFile = File(filesDir, "trusted_applications.json")

    private fun loadFromAssets(fileName: String): String? {
        return try {
            appContext.assets.open("data/$fileName").bufferedReader().use { it.readText() }
        } catch (_: Exception) {
            null
        }
    }

    private fun <T> saveToFile(file: File, data: T) {
        try {
            file.writeText(gson.toJson(data))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun <T> loadFromFile(file: File, typeToken: TypeToken<T>, assetFileName: String? = null): T? {
        var result: T? = null
        try {
            if (file.exists()) {
                val content = file.readText()
                if (content.isNotBlank()) {
                    result = gson.fromJson(content, typeToken.type)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            try { file.delete() } catch (_: Exception) {}
        }

        if (result == null && assetFileName != null) {
            try {
                loadFromAssets(assetFileName)?.let { assetData ->
                    result = gson.fromJson(assetData, typeToken.type)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }

    fun saveUsers(users: List<UserDto>) = saveToFile(usersFile, users)

    fun loadUsers(): List<UserDto> {
        val type = object : TypeToken<List<UserDto>>() {}
        return loadFromFile(usersFile, type, "users.json") ?: emptyList()
    }

    fun saveCommunities(communities: List<CommunityDto>) = saveToFile(communitiesFile, communities)

    fun loadCommunities(): List<CommunityDto> {
        val type = object : TypeToken<List<CommunityDto>>() {}
        return loadFromFile(communitiesFile, type, "communities.json") ?: emptyList()
    }

    fun saveForumMessages(communityId: Int, messages: List<ForumMessageDto>) {
        val file = File(filesDir, "forum_$communityId.json")
        saveToFile(file, messages)
    }

    fun loadForumMessages(communityId: Int): List<ForumMessageDto> {
        val file = File(filesDir, "forum_$communityId.json")
        val type = object : TypeToken<List<ForumMessageDto>>() {}
        return loadFromFile(file, type) ?: emptyList()
    }

    fun saveTrustedApplications(apps: List<TrustedApplicationDto>) = saveToFile(trustedAppsFile, apps)

    fun loadTrustedApplications(): List<TrustedApplicationDto> {
        val type = object : TypeToken<List<TrustedApplicationDto>>() {}
        return loadFromFile(trustedAppsFile, type) ?: emptyList()
    }
}
