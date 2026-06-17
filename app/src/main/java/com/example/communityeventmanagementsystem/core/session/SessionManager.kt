package com.example.communityeventmanagementsystem.core.session

import com.example.communityeventmanagementsystem.core.datastore.DataStoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val dataStoreManager: DataStoreManager
) {
    val authToken: Flow<String?> = dataStoreManager.authToken
    val userRole: Flow<String?> = dataStoreManager.userRole
    val userData: Flow<String?> = dataStoreManager.userData

    suspend fun isLoggedIn(): Boolean {
        return dataStoreManager.authToken.firstOrNull() != null
    }

    suspend fun logout() {
        dataStoreManager.clearSession()
    }
}
