// PlayerScreen.kt - Updated with Album Art and Real Song Data
package com.codewithprashant.musicapp

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlin.math.cos
import kotlin.math.sin
import com.codewithprashant.musicapp.ui.theme.*

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
            DeepNavy,
            MidnightBlue,
            DarkCharcoal,
            DeepNavy
        ),
        radius = 1200f
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

            Spacer(modifier = Modifier.height(40.dp))

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
                totalTime = formatTime(playerState.duration),
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

            Spacer(modifier = Modifier.height(40.dp))
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
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(48.dp)
                .glassCard(alpha = 0.12f, cornerRadius = 24.dp)
        ) {
            Icon(
                Icons.Rounded.KeyboardArrowDown,
                contentDescription = "Back",
                tint = TextPrimary,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = "Now Playing",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        IconButton(
            onClick = onMoreClick,
            modifier = Modifier
                .size(48.dp)
                .glassCard(alpha = 0.12f, cornerRadius = 24.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Rounded.QueueMusic,
                contentDescription = "Queue",
                tint = TextPrimary,
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
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "album_rotation")

    val rotation by if (isPlaying) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(25000, easing = LinearEasing),
                repeatMode = androidx.compose.animation.core.RepeatMode.Restart
            ),
            label = "rotation"
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    val scale by animateFloatAsState(
        targetValue = if (isPlaying) 1.05f else 1f,
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
        // Ambient glow background
        Box(
            modifier = Modifier
                .size(360.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            SoftPurple.copy(alpha = 0.15f),
                            SoftBlue.copy(alpha = 0.12f),
                            SoftPink.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        radius = 400f
                    ),
                    CircleShape
                )
        )

        // Main album art container
        Card(
            modifier = Modifier
                .size(300.dp)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    rotationZ = rotation
                ),
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 20.dp,
                pressedElevation = 24.dp
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Album art image
                if (song?.albumArt?.isNotEmpty() == true) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(song.albumArt)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Album Art",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.default_album_art),
                        placeholder = painterResource(id = R.drawable.default_album_art)
                    )
                } else {
                    // Default album art with gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        SoftPurple.copy(alpha = 0.4f),
                                        SoftBlue.copy(alpha = 0.3f),
                                        SoftPink.copy(alpha = 0.25f),
                                        SoftTeal.copy(alpha = 0.2f)
                                    )
                                ),
                                CircleShape
                            )
                            .border(
                                width = 2.dp,
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.3f),
                                        Color.White.copy(alpha = 0.1f)
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.MusicNote,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }

                // Center dot (vinyl record style)
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            Color.White.copy(alpha = 0.4f),
                            CircleShape
                        )
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.6f),
                            CircleShape
                        )
                )
            }
        }

        // Floating particles effect (only when playing)
        if (isPlaying) {
            repeat(8) { index ->
                val angle = (index * 45f)
                val animatedRadius by infiniteTransition.animateFloat(
                    initialValue = 160f,
                    targetValue = 180f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(3000 + index * 300, easing = EaseInOutSine),
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
                        .size(6.dp)
                        .background(
                            Color.White.copy(alpha = 0.3f),
                            CircleShape
                        )
                )
            }
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
            text = song?.title ?: "No Song Selected",
            color = TextPrimary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = song?.artist ?: "Unknown Artist",
            color = TextSecondary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (song?.album?.isNotEmpty() == true) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = song.album,
                color = TextTertiary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            IconButton(
                onClick = onLyricsClick,
                modifier = Modifier
                    .size(48.dp)
                    .glassCard(alpha = 0.12f, cornerRadius = 24.dp)
            ) {
                Icon(
                    Icons.Rounded.Lyrics,
                    contentDescription = "Lyrics",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                SoftPink.copy(alpha = 0.4f),
                                SoftPink.copy(alpha = 0.2f)
                            )
                        ),
                        CircleShape
                    )
                    .border(
                        1.dp,
                        SoftPink.copy(alpha = 0.3f),
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Rounded.Favorite,
                    contentDescription = "Favorite",
                    tint = SoftPink,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(48.dp)
                    .glassCard(alpha = 0.12f, cornerRadius = 24.dp)
            ) {
                Icon(
                    Icons.Rounded.Download,
                    contentDescription = "Download",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(48.dp)
                    .glassCard(alpha = 0.12f, cornerRadius = 24.dp)
            ) {
                Icon(
                    Icons.Rounded.Share,
                    contentDescription = "Share",
                    tint = TextPrimary,
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
                activeTrackColor = SoftPurple,
                inactiveTrackColor = GlassMedium
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = currentTime,
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = totalTime,
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
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
                .size(52.dp)
                .background(
                    if (isShuffleEnabled) {
                        Brush.radialGradient(
                            colors = listOf(
                                SoftBlue.copy(alpha = 0.4f),
                                SoftBlue.copy(alpha = 0.2f)
                            )
                        )
                    } else {
                        Brush.radialGradient(
                            colors = listOf(
                                GlassMedium,
                                GlassLight
                            )
                        )
                    },
                    CircleShape
                )
                .border(
                    1.dp,
                    if (isShuffleEnabled) SoftBlue.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f),
                    CircleShape
                )
        ) {
            Icon(
                Icons.Rounded.Shuffle,
                contentDescription = "Shuffle",
                tint = if (isShuffleEnabled) SoftBlue else TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }

        IconButton(
            onClick = onPrevious,
            modifier = Modifier
                .size(60.dp)
                .glassCard(alpha = 0.15f, cornerRadius = 30.dp)
        ) {
            Icon(
                Icons.Rounded.SkipPrevious,
                contentDescription = "Previous",
                tint = TextPrimary,
                modifier = Modifier.size(28.dp)
            )
        }

        Card(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            FloatingActionButton(
                onClick = onPlayPause,
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.9f),
                                    Color.White.copy(alpha = 0.7f)
                                )
                            ),
                            CircleShape
                        )
                        .border(
                            2.dp,
                            Color.White.copy(alpha = 0.3f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = DeepNavy,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        IconButton(
            onClick = onNext,
            modifier = Modifier
                .size(60.dp)
                .glassCard(alpha = 0.15f, cornerRadius = 30.dp)
        ) {
            Icon(
                Icons.Rounded.SkipNext,
                contentDescription = "Next",
                tint = TextPrimary,
                modifier = Modifier.size(28.dp)
            )
        }

        IconButton(
            onClick = onRepeatToggle,
            modifier = Modifier
                .size(52.dp)
                .background(
                    when (repeatMode) {
                        RepeatMode.ONE -> Brush.radialGradient(
                            colors = listOf(
                                SoftPink.copy(alpha = 0.4f),
                                SoftPink.copy(alpha = 0.2f)
                            )
                        )
                        RepeatMode.ALL -> Brush.radialGradient(
                            colors = listOf(
                                SoftGreen.copy(alpha = 0.4f),
                                SoftGreen.copy(alpha = 0.2f)
                            )
                        )
                        RepeatMode.OFF -> Brush.radialGradient(
                            colors = listOf(
                                GlassMedium,
                                GlassLight
                            )
                        )
                    },
                    CircleShape
                )
                .border(
                    1.dp,
                    when (repeatMode) {
                        RepeatMode.ONE -> SoftPink.copy(alpha = 0.3f)
                        RepeatMode.ALL -> SoftGreen.copy(alpha = 0.3f)
                        RepeatMode.OFF -> Color.White.copy(alpha = 0.1f)
                    },
                    CircleShape
                )
        ) {
            Icon(
                when (repeatMode) {
                    RepeatMode.ONE -> Icons.Rounded.RepeatOne
                    else -> Icons.Rounded.Repeat
                },
                contentDescription = "Repeat",
                tint = when (repeatMode) {
                    RepeatMode.ONE -> SoftPink
                    RepeatMode.ALL -> SoftGreen
                    RepeatMode.OFF -> TextSecondary
                },
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
                .size(44.dp)
                .glassCard(alpha = 0.12f, cornerRadius = 22.dp)
        ) {
            Icon(
                Icons.Rounded.Equalizer,
                contentDescription = "Equalizer",
                tint = TextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                Icons.AutoMirrored.Rounded.VolumeDown,
                contentDescription = "Volume Down",
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )

            Slider(
                value = volume,
                onValueChange = onVolumeChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = SoftPurple,
                    inactiveTrackColor = GlassMedium
                )
            )

            Icon(
                Icons.AutoMirrored.Rounded.VolumeUp,
                contentDescription = "Volume Up",
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }

        IconButton(
            onClick = onShareClick,
            modifier = Modifier
                .size(44.dp)
                .glassCard(alpha = 0.12f, cornerRadius = 22.dp)
        ) {
            Icon(
                Icons.Rounded.Share,
                contentDescription = "Share",
                tint = TextPrimary,
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
            .background(DeepNavy.copy(alpha = 0.95f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .glassCard(alpha = 0.15f, cornerRadius = 24.dp)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Lyrics",
                        color = TextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = song?.lyrics?.ifEmpty { "No lyrics available for this song" }
                            ?: "No lyrics available",
                        color = TextSecondary,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp
                    )
                }
            }
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
            .background(DeepNavy.copy(alpha = 0.95f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .glassCard(alpha = 0.15f, cornerRadius = 24.dp)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Queue",
                        color = TextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Coming Soon",
                        color = TextSecondary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun EqualizerOverlay(
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy.copy(alpha = 0.95f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .glassCard(alpha = 0.15f, cornerRadius = 24.dp)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Equalizer",
                        color = TextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Coming Soon",
                        color = TextSecondary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

private fun formatTime(milliseconds: Long): String {
    val seconds = (milliseconds / 1000) % 60
    val minutes = (milliseconds / (1000 * 60)) % 60
    return "${minutes}:${seconds.toString().padStart(2, '0')}"
}