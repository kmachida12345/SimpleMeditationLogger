package com.github.kmachida12345.simplemeditationlogger.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.kmachida12345.simplemeditationlogger.R
import com.github.kmachida12345.simplemeditationlogger.data.entity.MeditationSession
import com.github.kmachida12345.simplemeditationlogger.data.entity.durationFormatted
import com.github.kmachida12345.simplemeditationlogger.ui.theme.Dimensions
import com.github.kmachida12345.simplemeditationlogger.ui.theme.Primary
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateToHome: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.history_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { 
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = stringResource(R.string.home_title),
                            modifier = Modifier.size(Dimensions.IconSize.Medium)
                        ) 
                    },
                    label = { 
                        Text(
                            text = stringResource(R.string.home_title),
                            fontSize = 10.sp
                        ) 
                    },
                    selected = false,
                    onClick = onNavigateToHome
                )
                NavigationBarItem(
                    icon = { 
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = stringResource(R.string.history_title),
                            modifier = Modifier.size(Dimensions.IconSize.Medium)
                        ) 
                    },
                    label = { 
                        Text(
                            text = stringResource(R.string.history_title),
                            fontSize = 10.sp
                        ) 
                    },
                    selected = true,
                    onClick = { }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.sessions.isEmpty() -> {
                    EmptyState(modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = Dimensions.Spacing.Large, 
                            vertical = Dimensions.Spacing.Medium
                        )
                    ) {
                        items(uiState.sessions) { session ->
                            HistoryItem(session = session)
                            if (session != uiState.sessions.last()) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = Dimensions.Spacing.Medium),
                                    color = Color(0xFFF1F5F9)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(session: MeditationSession) {
    // 多言語対応の日付フォーマット
    val dateFormatter = if (Locale.getDefault().language == "ja") {
        DateTimeFormatter.ofPattern("yyyy年M月d日", Locale.JAPAN)
    } else {
        DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US)
    }
    
    val localDate = session.startTime.atZone(ZoneId.systemDefault()).toLocalDate()
    val formattedDate = localDate.format(dateFormatter)
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formattedDate,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Text(
            text = session.durationFormatted(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📊",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(Dimensions.Spacing.Medium))
        Text(
            text = "No meditation history yet",
            fontSize = 16.sp,
            color = Color.Gray
        )
        Text(
            text = "Start your first session!",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

