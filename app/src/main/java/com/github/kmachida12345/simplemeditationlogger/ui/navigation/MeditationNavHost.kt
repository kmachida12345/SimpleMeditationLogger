package com.github.kmachida12345.simplemeditationlogger.ui.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.github.kmachida12345.simplemeditationlogger.ui.complete.CompleteScreen
import com.github.kmachida12345.simplemeditationlogger.ui.countdown.CountdownScreen
import com.github.kmachida12345.simplemeditationlogger.ui.drawer.DrawerContent
import com.github.kmachida12345.simplemeditationlogger.ui.history.HistoryScreen
import com.github.kmachida12345.simplemeditationlogger.ui.home.HomeScreen
import kotlinx.coroutines.launch

@Composable
fun MeditationNavHost(
    navController: NavHostController = rememberNavController()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onClose = {
                    scope.launch { drawerState.close() }
                },
                onNavigateToDefaultTime = {
                    // TODO(feature): デフォルト時間設定ダイアログ
                    scope.launch { drawerState.close() }
                }
            )
        }
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
                        scope.launch { drawerState.open() }
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
                    onComplete = { actualDurationSeconds ->
                        navController.navigate(Screen.Complete(actualDurationSeconds)) {
                            popUpTo<Screen.Countdown> { inclusive = true }
                        }
                    }
                )
            }
            
            composable<Screen.Complete> { backStackEntry ->
                val complete = backStackEntry.toRoute<Screen.Complete>()
                CompleteScreen(
                    durationSeconds = complete.durationSeconds,
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
}
