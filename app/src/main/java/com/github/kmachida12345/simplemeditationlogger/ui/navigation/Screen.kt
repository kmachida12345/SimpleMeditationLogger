package com.github.kmachida12345.simplemeditationlogger.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Home : Screen
    
    @Serializable
    data object History : Screen
    
    @Serializable
    data class Countdown(val durationSeconds: Int) : Screen
    
    @Serializable
    data class Complete(val durationSeconds: Long) : Screen
}
