package com.github.kmachida12345.simplemeditationlogger.ui.complete

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.kmachida12345.simplemeditationlogger.R
import com.github.kmachida12345.simplemeditationlogger.ui.theme.Primary

@Composable
fun CompleteScreen(
    durationMinutes: Int,
    isHealthConnectSynced: Boolean = true,
    onBackToHome: () -> Unit
) {
    // Check icon pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconPulse"
    )
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Main Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Check Icon
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Primary.copy(alpha = 0.8f),
                    modifier = Modifier
                        .size(80.dp)
                        .scale(scale)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Duration Display
                Text(
                    text = formatDuration(durationMinutes),
                    fontSize = 96.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                    letterSpacing = (-4).sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Completion Message
                Text(
                    text = stringResource(R.string.complete_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Health Connect Badge
                if (isHealthConnectSynced) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF0F4F8)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.complete_health_saved),
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
            
            // Footer Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onBackToHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary
                    ),
                    shape = CircleShape
                ) {
                    Text(
                        text = stringResource(R.string.complete_back_home),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun formatDuration(durationMinutes: Int): String {
    // 分単位で表示（秒は切り捨て）
    val hours = durationMinutes / 60
    val minutes = durationMinutes % 60
    
    return if (hours > 0) {
        "%d:%02d".format(hours, minutes)
    } else {
        "%d:00".format(minutes)
    }
}
