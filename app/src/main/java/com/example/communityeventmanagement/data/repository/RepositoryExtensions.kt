package com.example.communityeventmanagement.data.repository

import android.util.Log
import com.example.communityeventmanagement.util.Resource

private const val TAG = "RepositoryExtensions"

/**
 * Executes a suspend function and wraps the result in a Resource.
 * Centralized to avoid redundancy in repositories.
 */
suspend fun <T> safeCall(call: suspend () -> T): Resource<T> {
    return try {
        Resource.Success(call())
    } catch (e: Exception) {
        Log.e(TAG, "Error in repository operation", e)
        Resource.Error(e.message ?: "An unknown error occurred")
    }
}

