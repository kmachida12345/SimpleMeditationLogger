package com.github.kmachida12345.simplemeditationlogger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.github.kmachida12345.simplemeditationlogger.ui.home.HomeScreen
import com.github.kmachida12345.simplemeditationlogger.ui.theme.SimpleMeditationLoggerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleMeditationLoggerTheme {
                HomeScreen(
                    onNavigateToHistory = { },
                    onOpenDrawer = { }
                )
            }
        }
    }
}
