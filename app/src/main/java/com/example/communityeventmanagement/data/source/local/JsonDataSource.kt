package com.example.communityeventmanagement.data.source.local

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class JsonDataSource(private val context: Context, private val baseDir: File? = null) {
    val tag = "JsonDataSource"
    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create()
    private val filesDir = baseDir ?: context.filesDir

    fun getLocalFile(fileName: String): File = 
        File(filesDir, fileName)
    
    fun localFileExists(fileName: String): Boolean = 
        getLocalFile(fileName).exists()

    suspend fun copyAssetToInternalStorage(fileName: String) {
        withContext(Dispatchers.IO) {
            val target = getLocalFile(fileName)
            if (target.exists()) return@withContext
            try {
                context.assets.open("data/$fileName").use { input ->
                    target.outputStream().use { output -> input.copyTo(output) }
                }
                Log.d(tag, "Copied: $fileName")
            } catch (e: Exception) {
                Log.e(tag, "Failed to copy asset: $fileName", e)
            }
        }
    }

    suspend inline fun <reified T> loadList(fileName: String): List<T> {
        if (!localFileExists(fileName)) copyAssetToInternalStorage(fileName)
        return withContext(Dispatchers.IO) {
            try {
                val json = getLocalFile(fileName).readText()
                Gson().fromJson<List<T>>(json, object : TypeToken<List<T>>() {}.type) ?: emptyList()
            } catch (e: Exception) {
                Log.e(tag, "Error loading list from $fileName", e)
                emptyList()
            }
        }
    }

    suspend fun <T> safeLoad(tag: String, block: suspend () -> T): T? {
        return try {
            block()
        } catch (e: Exception) {
            Log.e("JsonDataSource", "$tag: ${e.message}", e)
            null
        }
    }

    suspend fun <T> saveData(fileName: String, data: T) = withContext(Dispatchers.IO) {
        val file = getLocalFile(fileName)
        try {
            val json = gson.toJson(data)
            file.writeText(json)
            Log.d(tag, "Saved data to $fileName (${json.length} bytes)")
        } catch (e: Exception) {
            Log.e(tag, "Error saving to $fileName", e)
        }
    }

    // Specific save methods (loading is now generic via loadList)
    suspend fun saveUsers(users: List<com.example.communityeventmanagement.data.dto.UserDto>) = 
        saveData("users.json", users)

    suspend fun saveCommunities(communities: List<com.example.communityeventmanagement.data.dto.CommunityDto>) = 
        saveData("communities.json", communities)

    suspend fun saveTrustedApplications(apps: List<com.example.communityeventmanagement.data.dto.TrustedApplicationDto>) = 
        saveData("trusted_applications.json", apps)

    suspend fun saveAllForumMessages(messages: List<com.example.communityeventmanagement.data.dto.ForumMessageDto>) = 
        saveData("forum_messages.json", messages)
}
