package com.github.kmachida12345.simplemeditationlogger.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.github.kmachida12345.simplemeditationlogger.ui.complete.CompleteScreen
import com.github.kmachida12345.simplemeditationlogger.ui.countdown.CountdownScreen
import com.github.kmachida12345.simplemeditationlogger.ui.history.HistoryScreen
import com.github.kmachida12345.simplemeditationlogger.ui.home.HomeScreen

@Composable
fun MeditationNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home
    ) {
        composable<Screen.Home> {
            HomeScreen(
                onNavigateToHistory = {
                    navController.navigate(Screen.History)
                },
                onOpenDrawer = {
                    // TODO: ドロワー実装時に処理追加
                },
                onStartMeditation = { durationMinutes ->
                    navController.navigate(Screen.Countdown(durationMinutes))
                }
            )
        }
        
        composable<Screen.History> {
            HistoryScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo<Screen.Home> { inclusive = true }
                    }
                }
            )
        }
        
        composable<Screen.Countdown> { backStackEntry ->
            val countdown = backStackEntry.toRoute<Screen.Countdown>()
            CountdownScreen(
                onBack = {
                    navController.popBackStack()
                },
                onComplete = { actualDurationMinutes ->
                    navController.navigate(Screen.Complete(actualDurationMinutes)) {
                        popUpTo<Screen.Countdown> { inclusive = true }
                    }
                }
            )
        }
        
        composable<Screen.Complete> { backStackEntry ->
            val complete = backStackEntry.toRoute<Screen.Complete>()
            CompleteScreen(
                durationMinutes = complete.durationMinutes,
                isHealthConnectSynced = true,
                onBackToHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo<Screen.Home> { inclusive = true }
                    }
                }
            )
        }
    }
}
