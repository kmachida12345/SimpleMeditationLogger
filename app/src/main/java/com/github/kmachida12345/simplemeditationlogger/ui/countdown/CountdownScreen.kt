package com.github.kmachida12345.simplemeditationlogger.ui.countdown

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.github.kmachida12345.simplemeditationlogger.ui.theme.Primary

@Composable
fun CountdownScreen(
    onBack: () -> Unit,
    onComplete: () -> Unit,
    viewModel: CountdownViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // 完了時の遷移
    if (uiState.isCompleted) {
        onComplete()
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
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.close)
                    )
                }
                
                // Health Connect Badge
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFF0F4F8)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.home_health_connect).uppercase(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )
                    }
                }
                
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null
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
                Text(
                    text = stringResource(R.string.countdown_title),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 64.dp)
                )
                
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
                                text = formatTime(uiState.remainingSeconds),
                                fontSize = 88.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary,
                                letterSpacing = (-4).sp
                            )
                            Text(
                                text = stringResource(R.string.countdown_remaining).uppercase(),
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

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return "%d:%02d".format(minutes, secs)
}
