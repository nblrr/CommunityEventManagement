package com.example.communityeventmanagement

import android.app.Application
import com.example.communityeventmanagement.data.repository.AppContainer
import com.example.communityeventmanagement.data.repository.DefaultAppContainer

class CommunityApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
