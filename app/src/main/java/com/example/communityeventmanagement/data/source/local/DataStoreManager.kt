package com.example.communityeventmanagement.data.source.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreManager(private val context: Context) {

    companion object {
        private val SESSION_USER_ID = stringPreferencesKey("session_user_id")
        private val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val sessionUserId: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[SESSION_USER_ID]
        }

    suspend fun saveSession(userId: String?) {
        context.dataStore.edit { preferences ->
            if (userId != null) {
                preferences[SESSION_USER_ID] = userId
            } else {
                preferences.remove(SESSION_USER_ID)
            }
        }
    }

    val themeMode: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[THEME_MODE]
        }

    suspend fun saveTheme(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode
        }
    }
}
