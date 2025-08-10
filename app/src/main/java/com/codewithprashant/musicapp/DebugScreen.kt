// DebugScreen.kt - Troubleshooting Screen
package com.codewithprashant.musicapp

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithprashant.musicapp.ui.theme.*
import android.content.Intent
import android.net.Uri
import android.provider.Settings

@Composable
fun DebugScreen(
    libraryState: LibraryState,
    playerState: MusicPlayerState,
    onRefreshLibrary: () -> Unit,
    onDebugPrint: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(DeepNavy, MidnightBlue, DarkCharcoal)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(48.dp)
                        .glassCard(alpha = 0.12f, cornerRadius = 24.dp)
                ) {
                    Icon(
                        Icons.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Debug Info",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Permission Status
                item {
                    DebugCard(
                        title = "Permissions",
                        icon = Icons.Rounded.Security,
                        color = if (libraryState.hasPermission) SoftGreen else SoftPink
                    ) {
                        DebugItem("Read Audio Permission", if (libraryState.hasPermission) "✅ Granted" else "❌ Denied")

                        if (!libraryState.hasPermission) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = Uri.fromParts("package", context.packageName, null)
                                    }
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SoftPink)
                            ) {
                                Text("Open App Settings")
                            }
                        }
                    }
                }

                // Library Status
                item {
                    DebugCard(
                        title = "Music Library",
                        icon = Icons.Rounded.LibraryMusic,
                        color = when {
                            libraryState.isLoading -> SoftBlue
                            libraryState.songs.isNotEmpty() -> SoftGreen
                            else -> SoftPink
                        }
                    ) {
                        DebugItem("Loading", if (libraryState.isLoading) "Yes" else "No")
                        DebugItem("Songs Found", "${libraryState.songs.size}")
                        DebugItem("Artists Found", "${libraryState.artists.size}")
                        DebugItem("Albums Found", "${libraryState.albums.size}")
                        DebugItem("Scan Progress", "${(libraryState.scanProgress * 100).toInt()}%")

                        if (libraryState.error != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Error: ${libraryState.error}",
                                color = SoftPink,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onRefreshLibrary,
                            colors = ButtonDefaults.buttonColors(containerColor = SoftBlue)
                        ) {
                            Text("Refresh Library")
                        }
                    }
                }

                // Player Status
                item {
                    DebugCard(
                        title = "Music Player",
                        icon = Icons.Rounded.PlayCircle,
                        color = if (playerState.currentSong != null) SoftGreen else SoftTeal
                    ) {
                        DebugItem("Current Song", playerState.currentSong?.title ?: "None")
                        DebugItem("Is Playing", if (playerState.isPlaying) "Yes" else "No")
                        DebugItem("Duration", "${playerState.duration}ms")
                        DebugItem("Position", "${playerState.currentPosition}ms")
                        DebugItem("Progress", "${(playerState.progress * 100).toInt()}%")

                        if (playerState.error != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Error: ${playerState.error}",
                                color = SoftPink,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Sample Songs
                if (libraryState.songs.isNotEmpty()) {
                    item {
                        DebugCard(
                            title = "Sample Songs",
                            icon = Icons.Rounded.MusicNote,
                            color = SoftPurple
                        ) {
                            libraryState.songs.take(3).forEach { song ->
                                DebugSongItem(song)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                // Actions
                item {
                    DebugCard(
                        title = "Actions",
                        icon = Icons.Rounded.Build,
                        color = SoftTeal
                    ) {
                        Button(
                            onClick = onDebugPrint,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = SoftTeal)
                        ) {
                            Text("Print Debug Log")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = SoftBlue)
                        ) {
                            Text("Open App Settings")
                        }
                    }
                }

                // Common Issues
                item {
                    DebugCard(
                        title = "Troubleshooting Tips",
                        icon = Icons.Rounded.Help,
                        color = SoftBlue
                    ) {
                        TroubleshootingTip("No songs found?", "1. Check if device has music files\n2. Grant storage permissions\n3. Restart the app")
                        TroubleshootingTip("Can't play music?", "1. Verify file formats (MP3, AAC)\n2. Check file paths exist\n3. Restart app")
                        TroubleshootingTip("No album art?", "This is normal - not all files have embedded artwork")
                    }
                }
            }
        }
    }
}

@Composable
fun DebugCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .glassCard(alpha = 0.1f, cornerRadius = 16.dp)
                .padding(16.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color.copy(alpha = 0.3f),
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = title,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                content()
            }
        }
    }
}

@Composable
fun DebugItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun DebugSongItem(song: Song) {
    Column {
        Text(
            text = song.title,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "Artist: ${song.artist}",
            color = TextSecondary,
            fontSize = 12.sp
        )
        Text(
            text = "Path: ${song.filePath}",
            color = TextTertiary,
            fontSize = 10.sp
        )
    }
}

@Composable
fun TroubleshootingTip(title: String, description: String) {
    Column(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = description,
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    }
}