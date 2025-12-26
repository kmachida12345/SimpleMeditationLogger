package com.github.kmachida12345.simplemeditationlogger.ui.home

import androidx.annotation.Dimension
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.github.kmachida12345.simplemeditationlogger.R
import com.github.kmachida12345.simplemeditationlogger.ui.theme.Dimensions
import com.github.kmachida12345.simplemeditationlogger.ui.theme.Primary
import com.github.kmachida12345.simplemeditationlogger.ui.theme.SlateBlue100
import com.github.kmachida12345.simplemeditationlogger.ui.theme.SlateBlue400
import com.github.kmachida12345.simplemeditationlogger.ui.theme.SlateBlue800
import java.util.Locale

@Composable
fun HomeScreen(
    onNavigateToHistory: () -> Unit,
    onOpenDrawer: () -> Unit,
    onStartMeditation: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { 
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = stringResource(R.string.home_title),
                            modifier = Modifier.size(26.dp)
                        ) 
                    },
                    label = { 
                        Text(
                            text = stringResource(R.string.home_title),
                            fontSize = 10.sp
                        ) 
                    },
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { 
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = stringResource(R.string.history_title),
                            modifier = Modifier.size(26.dp)
                        ) 
                    },
                    label = { 
                        Text(
                            text = stringResource(R.string.history_title),
                            fontSize = 10.sp
                        ) 
                    },
                    selected = false,
                    onClick = onNavigateToHistory
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = stringResource(R.string.home_menu),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.width(40.dp))
                }
                
                // Main Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    // Main Timer Button
                    FilledTonalButton(
                        onClick = { onStartMeditation(uiState.defaultDurationSeconds) },
                        modifier = Modifier
                            .size(Dimensions.ButtonSize.TimerButton)
                            .shadow(24.dp, CircleShape),
                        shape = CircleShape,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(Dimensions.IconSize.ExtraLarge)
                            )
                            Spacer(modifier = Modifier.height(Dimensions.Spacing.ExtraSmall))
                            Text(
                                text = formatDurationForDisplay(uiState.defaultDurationSeconds),
                                fontSize = Dimensions.FontSize.Timer,
                                fontWeight = FontWeight.Light,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stringResource(R.string.home_min),
                                fontSize = Dimensions.FontSize.Small,
                                fontWeight = FontWeight.Medium,
                                color = SlateBlue400,
                                letterSpacing = 0.2.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    // Time Adjust Button
                    OutlinedButton(
                        onClick = { viewModel.onAdjustTime() },
                        modifier = Modifier.size(176.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFFF0F4F8)
                        )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.home_time_adjust),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Health Connect Badge
                    if (uiState.isHealthConnectEnabled) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.border(1.dp, SlateBlue100, CircleShape)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.home_health_connect).uppercase(Locale.ROOT),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateBlue400
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatDurationForDisplay(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return if (remainingSeconds == 0) {
        "$minutes"
    } else {
        "$minutes:${remainingSeconds.toString().padStart(2, '0')}"
    }
}
