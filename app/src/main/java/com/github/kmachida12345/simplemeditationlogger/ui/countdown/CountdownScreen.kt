package com.github.kmachida12345.simplemeditationlogger.ui.countdown

import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.kmachida12345.simplemeditationlogger.R
import com.github.kmachida12345.simplemeditationlogger.domain.model.HealthConnectSyncStatus
import com.github.kmachida12345.simplemeditationlogger.ui.theme.Primary
import com.github.kmachida12345.simplemeditationlogger.ui.util.formatMinutesSeconds
import java.util.Locale

@Composable
fun CountdownScreen(
    onBack: () -> Unit,
    onComplete: (durationSeconds: Int, syncStatus: HealthConnectSyncStatus, sessionSaved: Boolean) -> Unit,
    viewModel: CountdownViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 完了時の遷移(再コンポーズでの多重発火を防ぐためLaunchedEffectで一度だけ)
    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted) {
            onComplete(uiState.actualDurationSeconds, uiState.syncStatus, uiState.sessionSaved)
        }
    }

    // 瞑想中は画面を消灯させない
    val activity = LocalActivity.current
    DisposableEffect(Unit) {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // 誤操作でセッションを破棄しないよう、戻る操作は確認ダイアログを挟む
    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = !uiState.isCompleted) {
        showExitDialog = true
    }
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(stringResource(R.string.countdown_exit_title)) },
            text = { Text(stringResource(R.string.countdown_exit_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    onBack()
                }) {
                    Text(stringResource(R.string.countdown_exit_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showExitDialog = true }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.close)
                    )
                }
            }
            
            // Main Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Timer Display with Ripple Animation
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    // Ripple circles
                    RippleEffect()
                    
                    // Timer Circle
                    Surface(
                        modifier = Modifier.size(320.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 8.dp
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = formatMinutesSeconds(uiState.remainingSeconds),
                                fontSize = 88.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary,
                                letterSpacing = (-4).sp
                            )
                            Text(
                                text = stringResource(R.string.countdown_remaining).uppercase(Locale.ROOT),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray,
                                letterSpacing = 2.sp,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    }
                }
            }
            
            // Footer Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // End Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FilledIconButton(
                        onClick = { viewModel.onEnd() },
                        modifier = Modifier.size(56.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color(0xFFF0F4F8)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.countdown_end),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                }
                
                Spacer(modifier = Modifier.width(48.dp))
                
                // Play/Pause Button
                FloatingActionButton(
                    onClick = { viewModel.onPauseResume() },
                    modifier = Modifier.size(96.dp),
                    containerColor = Primary,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = if (uiState.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (uiState.isPaused) 
                            stringResource(R.string.countdown_resume) 
                        else 
                            stringResource(R.string.countdown_pause),
                        modifier = Modifier.size(48.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(48.dp))
                
                // Spacer for symmetry
                Box(modifier = Modifier.size(56.dp))
            }
        }
    }
}

@Composable
private fun RippleEffect() {
    val infiniteTransition = rememberInfiniteTransition(label = "ripple")
    
    repeat(3) { index ->
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 1.6f,
            animationSpec = infiniteRepeatable(
                animation = tween(5000, easing = CubicBezierEasing(0f, 0f, 0.2f, 1f)),
                repeatMode = RepeatMode.Restart,
                initialStartOffset = StartOffset(index * 1500)
            ),
            label = "ripple$index"
        )
        
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.6f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(5000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
                initialStartOffset = StartOffset(index * 1500)
            ),
            label = "alpha$index"
        )
        
        Box(
            modifier = Modifier
                .size(288.dp)
                .scale(scale)
                .border(1.dp, Primary.copy(alpha = alpha), CircleShape)
        )
    }
}
