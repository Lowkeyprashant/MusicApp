package com.codewithprashant.musicapp

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PlayerScreen(
    song: Song?,
    playerState: MusicPlayerState,
    onBackClick: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Float) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onRepeatToggle: () -> Unit,
    onShuffleToggle: () -> Unit
) {
    var showLyrics by remember { mutableStateOf(false) }
    var showQueue by remember { mutableStateOf(false) }
    var showEqualizer by remember { mutableStateOf(false) }

    val backgroundGradient = Brush.radialGradient(
        colors = listOf(
            Color(0xFF1A1A2E),
            Color(0xFF16213E),
            Color(0xFF0F3460),
            Color(0xFF000000)
        ),
        radius = 1000f
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            PlayerTopBar(
                onBackClick = onBackClick,
                onMoreClick = { showQueue = true }
            )

            Spacer(modifier = Modifier.height(32.dp))

            AlbumArtSection(
                song = song,
                isPlaying = playerState.isPlaying,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            SongInfoSection(
                song = song,
                onLyricsClick = { showLyrics = true },
                onFavoriteClick = { }
            )

            Spacer(modifier = Modifier.height(24.dp))

            ProgressSection(
                progress = playerState.progress,
                currentTime = formatTime(playerState.currentPosition),
                totalTime = song?.duration ?: "0:00",
                onSeek = onSeek
            )

            Spacer(modifier = Modifier.height(32.dp))

            PlayerControlsSection(
                isPlaying = playerState.isPlaying,
                isShuffleEnabled = playerState.isShuffleEnabled,
                repeatMode = playerState.repeatMode,
                onPlayPause = onPlayPause,
                onNext = onNext,
                onPrevious = onPrevious,
                onShuffleToggle = onShuffleToggle,
                onRepeatToggle = onRepeatToggle
            )

            Spacer(modifier = Modifier.height(24.dp))

            BottomControlsSection(
                volume = playerState.volume,
                onVolumeChange = onVolumeChange,
                onEqualizerClick = { showEqualizer = true },
                onShareClick = { }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showLyrics) {
            LyricsOverlay(
                song = song,
                onDismiss = { showLyrics = false }
            )
        }

        if (showQueue) {
            QueueOverlay(
                onDismiss = { showQueue = false }
            )
        }

        if (showEqualizer) {
            EqualizerOverlay(
                onDismiss = { showEqualizer = false }
            )
        }
    }
}

@Composable
fun PlayerTopBar(
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .background(
                    color = Color.White.copy(alpha = 0.1f),
                    CircleShape
                )
        ) {
            Icon(
                Icons.Rounded.KeyboardArrowDown,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = "Now Playing",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        IconButton(
            onClick = onMoreClick,
            modifier = Modifier
                .background(
                    color = Color.White.copy(alpha = 0.1f),
                    CircleShape
                )
        ) {
            Icon(
                Icons.Rounded.QueueMusic,
                contentDescription = "Queue",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun AlbumArtSection(
    song: Song?,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "album_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        ),
        label = "rotation"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPlaying) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // Glow effect background
        Box(
            modifier = Modifier
                .size(320.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF6366F1).copy(alpha = 0.2f), // Reduced alpha for subtlety
                            Color(0xFF8B5CF6).copy(alpha = 0.2f),
                            Color(0xFFEC4899).copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        radius = 400f
                    ),
                    CircleShape
                ) // Removed blur, as radial gradient with low alpha creates a soft glow
        )

        // Vinyl record effect
        Box(
            modifier = Modifier
                .size(280.dp)
                .scale(scale)
                .rotate(if (isPlaying) rotation else 0f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1A1A2E).copy(alpha = 0.8f), // Darker, more solid look
                            Color(0xFF16213E).copy(alpha = 0.9f),
                            Color(0xFF0F3460) // Solid base
                        )
                    ), // Use brush for a slight gradient effect
                    CircleShape
                )
                .border(2.dp, Color.White.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Inner album art
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF667EEA),
                                Color(0xFF764BA2).copy(alpha = 0.8f),
                                Color(0xFFF093FB).copy(alpha = 0.6f)
                            ) // More vibrant album art colors
                        ),
                        CircleShape
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.MusicNote,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }

            // Center dot
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(color = Color.White.copy(alpha = 0.3f), shape = CircleShape)
            )
        }

        // Floating particles effect
        repeat(6) { index ->
            val angle = (index * 60f)
            val animatedRadius by infiniteTransition.animateFloat(
                initialValue = 140f,
                targetValue = 160f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000 + index * 200, easing = EaseInOutSine),
                    repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
                ),
                label = "particle_$index"
            )

            val angleRadians = Math.toRadians(angle.toDouble())
            val x = animatedRadius * cos(angleRadians).toFloat()
            val y = animatedRadius * sin(angleRadians).toFloat()

            Box(
                modifier = Modifier
                    .offset(x.dp, y.dp)
                    .size(8.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.4f),
                        CircleShape
                    )
            )
        }
    }
}

@Composable
fun SongInfoSection(
    song: Song?,
    onLyricsClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = song?.title ?: "Unknown Song",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = song?.artist ?: "Unknown Artist",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            IconButton(
                onClick = onLyricsClick,
                modifier = Modifier
                    .background(
                        color = Color.White.copy(alpha = 0.1f),
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Rounded.Lyrics,
                    contentDescription = "Lyrics",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFEC4899),
                                Color(0xFFBE185D)
                            )
                        ),
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Rounded.Favorite,
                    contentDescription = "Favorite",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = { },
                modifier = Modifier
                    .background(
                        color = Color.White.copy(alpha = 0.1f),
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Rounded.Download,
                    contentDescription = "Download",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ProgressSection(
    progress: Float,
    currentTime: String,
    totalTime: String,
    onSeek: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
    ) {
        Slider(
            value = progress,
            onValueChange = onSeek,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = currentTime,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
            Text(
                text = totalTime,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun PlayerControlsSection(
    isPlaying: Boolean,
    isShuffleEnabled: Boolean,
    repeatMode: RepeatMode,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onShuffleToggle: () -> Unit,
    onRepeatToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onShuffleToggle,
            modifier = Modifier
                .background(
                    brush = if (isShuffleEnabled) {
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF6366F1),
                                Color(0xFF8B5CF6)
                            )
                        )
                    } else {
                        Brush.radialGradient(colors = listOf(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.1f))) // Use brush for consistency
                    },
                    CircleShape
                )
                .size(48.dp)
        ) {
            Icon(
                Icons.Rounded.Shuffle,
                contentDescription = "Shuffle",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        IconButton(
            onClick = onPrevious,
            modifier = Modifier
                .background(
                    color = Color.White.copy(alpha = 0.1f),
                    CircleShape
                )
                .size(56.dp)
        ) {
            Icon(
                Icons.Rounded.SkipPrevious,
                contentDescription = "Previous",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        FloatingActionButton(
            onClick = onPlayPause,
            modifier = Modifier.size(72.dp),
            containerColor = Color.Transparent,
            elevation = FloatingActionButtonDefaults.elevation(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White,
                                Color.White.copy(alpha = 0.8f)
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color(0xFF1A1A2E),
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        IconButton(
            onClick = onNext,
            modifier = Modifier
                .background(
                    color = Color.White.copy(alpha = 0.1f),
                    CircleShape
                )
                .size(56.dp)
        ) {
            Icon(
                Icons.Rounded.SkipNext,
                contentDescription = "Next",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        IconButton(
            onClick = onRepeatToggle,
            modifier = Modifier
                .background(
                    brush = when (repeatMode) {
                        RepeatMode.ONE -> Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFEC4899),
                                Color(0xFFBE185D)
                            )
                        )
                        RepeatMode.ALL -> Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF10B981),
                                Color(0xFF059669)
                            )
                        )
                        RepeatMode.OFF -> Brush.radialGradient(colors = listOf(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.1f)))
                    },
                    CircleShape
                )
                .size(48.dp)
        ) {
            Icon(
                when (repeatMode) {
                    RepeatMode.ONE -> Icons.Rounded.RepeatOne
                    else -> Icons.Rounded.Repeat
                },
                contentDescription = "Repeat",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun BottomControlsSection(
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    onEqualizerClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onEqualizerClick,
            modifier = Modifier
                .background(
                    color = Color.White.copy(alpha = 0.1f),
                    CircleShape
                )
        ) {
            Icon(
                Icons.Rounded.Equalizer,
                contentDescription = "Equalizer",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                Icons.Rounded.VolumeDown,
                contentDescription = "Volume Down",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(16.dp)
            )

            Slider(
                value = volume,
                onValueChange = onVolumeChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                )
            )

            Icon(
                Icons.Rounded.VolumeUp,
                contentDescription = "Volume Up",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(16.dp)
            )
        }

        IconButton(
            onClick = onShareClick,
            modifier = Modifier
                .background(
                    color = Color.White.copy(alpha = 0.1f),
                    CircleShape
                )
        ) {
            Icon(
                Icons.Rounded.Share,
                contentDescription = "Share",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun LyricsOverlay(
    song: Song?,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Lyrics",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = song?.lyrics?.ifEmpty { "No lyrics available for this song" }
                    ?: "No lyrics available",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
fun QueueOverlay(
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Queue Coming Soon",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun EqualizerOverlay(
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Equalizer Coming Soon",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatTime(milliseconds: Long): String {
    val seconds = (milliseconds / 1000) % 60
    val minutes = (milliseconds / (1000 * 60)) % 60
    return String.format("%d:%02d", minutes, seconds)
}