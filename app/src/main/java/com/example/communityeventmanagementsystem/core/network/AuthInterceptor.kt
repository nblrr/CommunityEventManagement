package com.example.communityeventmanagementsystem.core.network

import com.example.communityeventmanagementsystem.core.session.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { sessionManager.authToken.firstOrNull() }
        val request = chain.request().newBuilder().apply {
            token?.let {
                addHeader("Authorization", "Bearer $it")
            }
            addHeader("Accept", "application/json")
        }.build()
        return chain.proceed(request)
    }
}
