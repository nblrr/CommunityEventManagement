package com.example.communityeventmanagementsystem.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class DataStoreManager @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val USER_ROLE = stringPreferencesKey("user_role")
        val USER_DATA = stringPreferencesKey("user_data")
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN] = token
        }
    }

    val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[AUTH_TOKEN]
    }

    suspend fun saveRole(role: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ROLE] = role
        }
    }

    val userRole: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_ROLE]
    }

    suspend fun saveUserData(json: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_DATA] = json
        }
    }

    val userData: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_DATA]
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(AUTH_TOKEN)
            preferences.remove(USER_ROLE)
            preferences.remove(USER_DATA)
        }
    }
}
