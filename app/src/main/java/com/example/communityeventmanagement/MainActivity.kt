package com.example.communityeventmanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.communityeventmanagement.data.repository.AppState
import com.example.communityeventmanagement.navigation.AppNavigation
import com.example.communityeventmanagement.ui.components.FullScreenLoading
import com.example.communityeventmanagement.ui.theme.CommunityEventManagementTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        var isLoading by mutableStateOf(true)
        
        lifecycleScope.launch {
            AppState.initialize(this@MainActivity)
            isLoading = false
        }

        enableEdgeToEdge()
        setContent {
            CommunityEventManagementTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isLoading) {
                        FullScreenLoading(message = "Memulai Communitix...")
                    } else {
                        AppNavigation()
                    }
                }
            }
        }
    }
}
