package com.example.communityeventmanagement.data.local

import android.content.Context
import com.example.communityeventmanagement.data.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class JsonStorage(context: Context?, manualFilesDir: File? = null) {
    private val appContext = context?.applicationContext
    private val gson = Gson()
    private val filesDir: File = manualFilesDir ?: context!!.filesDir
    private val usersFile = File(filesDir, "users.json")
    private val communitiesFile = File(filesDir, "communities.json")
    private val sessionFile = File(filesDir, "session.json")

    private fun loadFromAssets(fileName: String): String? {
        return try {
            appContext?.assets?.open("data/$fileName")?.bufferedReader()?.use { it.readText() }
        } catch (_: Exception) {
            null
        }
    }

    fun saveSession(userId: String?) {
        sessionFile.writeText(userId ?: "")
    }

    fun loadSession(): String? {
        if (!sessionFile.exists()) return null
        val id = sessionFile.readText().trim()
        return id.ifEmpty { null }
    }

    fun saveUsers(users: List<UserProfile>) {
        usersFile.writeText(gson.toJson(users))
    }

    fun loadUsers(): List<UserProfile> {
        if (!usersFile.exists()) {
            val assetData = loadFromAssets("users.json")
            if (assetData != null) return gson.fromJson(assetData, object : TypeToken<List<UserProfile>>() {}.type)
            return emptyList()
        }
        val type = object : TypeToken<List<UserProfile>>() {}.type
        return try {
            gson.fromJson(usersFile.readText(), type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveCommunities(communities: List<Community>) {
        communitiesFile.writeText(gson.toJson(communities))
    }

    fun loadCommunities(): List<Community> {
        if (!communitiesFile.exists()) {
            val assetData = loadFromAssets("communities.json")
            if (assetData != null) return gson.fromJson(assetData, object : TypeToken<List<Community>>() {}.type)
            return emptyList()
        }
        val type = object : TypeToken<List<Community>>() {}.type
        return try {
            gson.fromJson(communitiesFile.readText(), type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveForumMessages(communityId: Int, messages: List<ForumMessage>) {
        val file = File(filesDir, "forum_$communityId.json")
        file.writeText(gson.toJson(messages))
    }

    fun loadForumMessages(communityId: Int): List<ForumMessage> {
        val file = File(filesDir, "forum_$communityId.json")
        if (!file.exists()) return emptyList()
        val type = object : TypeToken<List<ForumMessage>>() {}.type
        return try {
            gson.fromJson(file.readText(), type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }
}
