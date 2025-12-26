package com.github.kmachida12345.simplemeditationlogger.ui.drawer

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.kmachida12345.simplemeditationlogger.BuildConfig
import com.github.kmachida12345.simplemeditationlogger.R
import com.github.kmachida12345.simplemeditationlogger.ui.theme.Dimensions
import com.github.kmachida12345.simplemeditationlogger.ui.theme.Primary
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import java.util.Locale

@Composable
fun DrawerContent(
    onClose: () -> Unit,
    viewModel: DrawerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Time Picker Dialog
    if (uiState.showTimePickerDialog) {
        TimePickerDialog(
            currentMinutes = uiState.defaultMeditationSeconds / 60,
            onDismiss = { viewModel.onDismissTimePickerDialog() },
            onConfirm = { seconds -> viewModel.onTimeSelected(seconds) }
        )
    }
    
    ModalDrawerSheet(
        modifier = Modifier.width(320.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Medium)
                ) {
                    Surface(
                        modifier = Modifier.size(Dimensions.ButtonSize.Small),
                        shape = CircleShape,
                        color = Primary.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Spa,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(Dimensions.IconSize.Medium)
                            )
                        }
                    }
                    Text(
                        text = "Meditation",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Dimensions.Spacing.Medium))
            
            // Settings Section
            SectionHeader(stringResource(R.string.drawer_settings))
            
            DrawerMenuItem(
                icon = Icons.Default.Timer,
                title = stringResource(R.string.drawer_default_time),
                subtitle = formatDurationDisplay(uiState.defaultMeditationSeconds),
                onClick = { viewModel.onDefaultTimeClick() }
            )
            
            DrawerMenuItemWithSwitch(
                icon = Icons.Default.Favorite,
                title = stringResource(R.string.drawer_health_connect),
                checked = uiState.isHealthConnectEnabled,
                onCheckedChange = { viewModel.onHealthConnectToggle(it) }
            )
            
            Spacer(modifier = Modifier.height(Dimensions.Spacing.Small))
            HorizontalDivider(modifier = Modifier.padding(horizontal = Dimensions.Spacing.Medium))
            Spacer(modifier = Modifier.height(Dimensions.Spacing.Small))
            
            // Support Section
            SectionHeader(stringResource(R.string.drawer_support))
            
            DrawerMenuItem(
                icon = Icons.Default.Info,
                title = stringResource(R.string.drawer_about),
                // TODO(feature): About画面実装
                onClick = { }
            )
            
            DrawerMenuItem(
                icon = Icons.Default.Shield,
                title = stringResource(R.string.drawer_privacy),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://kmachida12345.github.io/SimpleMeditationLogger/privacy_policy.html")
                    }
                    context.startActivity(intent)
                }
            )
            
            DrawerMenuItem(
                icon = Icons.Default.Description,
                title = stringResource(R.string.drawer_licenses),
                onClick = {
                    context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
                }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Footer Version
            Text(
                text = stringResource(R.string.drawer_version, BuildConfig.VERSION_NAME),
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text.uppercase(Locale.ROOT),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        letterSpacing = 1.sp
    )
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.Spacing.Medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Medium)
        ) {
            Surface(
                modifier = Modifier.size(Dimensions.ButtonSize.Small),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF0F4F8)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(Dimensions.IconSize.Small)
                    )
                }
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            if (subtitle != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.ExtraSmall)
                ) {
                    Text(
                        text = subtitle,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(Dimensions.IconSize.Small)
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawerMenuItemWithSwitch(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.Spacing.Medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Medium)
        ) {
            Surface(
                modifier = Modifier.size(Dimensions.ButtonSize.Small),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF0F4F8)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(Dimensions.IconSize.Small)
                    )
                }
            }
            
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.height(28.dp)
            )
        }
    }
}

@Composable
private fun TimePickerDialog(
    currentMinutes: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var selectedMinutes by remember { mutableIntStateOf(currentMinutes) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.set_default_time)) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "$selectedMinutes min",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                Spacer(modifier = Modifier.height(Dimensions.Spacing.Medium))
                Slider(
                    value = selectedMinutes.toFloat(),
                    onValueChange = { selectedMinutes = it.toInt() },
                    valueRange = 1f..60f,
                    steps = 58
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("1 min", fontSize = 12.sp, color = Color.Gray)
                    Text("60 min", fontSize = 12.sp, color = Color.Gray)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedMinutes * 60) }) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

private fun formatDurationDisplay(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return if (remainingSeconds == 0) {
        "$minutes min"
    } else {
        "$minutes:${remainingSeconds.toString().padStart(2, '0')}"
    }
}
