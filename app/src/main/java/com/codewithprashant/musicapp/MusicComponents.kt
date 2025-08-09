// MusicComponents.kt - Complete Glassmorphism Implementation
package com.codewithprashant.musicapp

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithprashant.musicapp.ui.theme.*
import kotlin.random.Random
import androidx.compose.ui.unit.Dp

@Composable
fun AlbumArtCard(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    onClick: () -> Unit = {}
) {
    val rotation by animateFloatAsState(
        targetValue = if (isPlaying) 360f else 0f,
        animationSpec = tween(
            durationMillis = if (isPlaying) 10000 else 0,
            delayMillis = 0
        ),
        label = "Album rotation"
    )

    Card(
        modifier = modifier
            .size(200.dp)
            .clickable { onClick() },
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = glassmorphismCardElevation()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            SoftPurple.copy(alpha = 0.4f),
                            SoftBlue.copy(alpha = 0.3f),
                            SoftPink.copy(alpha = 0.2f)
                        )
                    )
                )
                .rotate(rotation),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Album,
                contentDescription = "Album Art",
                tint = TextPrimary,
                modifier = Modifier.size(80.dp)
            )
        }
    }
}

@Composable
fun EqualizerVisualization(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    barCount: Int = 5
) {
    var animationValues by remember { mutableStateOf(List(barCount) { 0.3f }) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isPlaying) {
                animationValues = List(barCount) { Random.nextFloat() * 0.9f + 0.1f }
                kotlinx.coroutines.delay(200)
            }
        } else {
            animationValues = List(barCount) { 0.1f }
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        animationValues.forEachIndexed { index, height ->
            val animatedHeight by animateFloatAsState(
                targetValue = height,
                animationSpec = tween(200),
                label = "Bar height $index"
            )

            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height((40 * animatedHeight).dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                SoftPurple,
                                SoftBlue,
                                SoftTeal
                            )
                        ),
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

@Composable
fun CircularProgressPlayer(
    modifier: Modifier = Modifier,
    progress: Float,
    isPlaying: Boolean,
    onPlayPause: () -> Unit
) {
    Box(
        modifier = modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawCircle(
                color = GlassMedium,
                radius = size.minDimension / 2,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )

            drawArc(
                color = SoftPurple,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        FloatingActionButton(
            onClick = onPlayPause,
            modifier = Modifier.size(56.dp),
            containerColor = Color.Transparent,
            elevation = FloatingActionButtonDefaults.elevation(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                SoftPurple.copy(alpha = 0.8f),
                                SoftBlue.copy(alpha = 0.6f)
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = TextPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun GenreChip(
    genre: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val backgroundColor = when (genre.lowercase()) {
        "rock" -> Brush.horizontalGradient(
            colors = listOf(
                Color(0xFFDC2626).copy(alpha = 0.4f),
                Color(0xFFEF4444).copy(alpha = 0.3f)
            )
        )
        "pop" -> Brush.horizontalGradient(
            colors = listOf(
                SoftPink.copy(alpha = 0.4f),
                SoftPurple.copy(alpha = 0.3f)
            )
        )
        "electronic" -> Brush.horizontalGradient(
            colors = listOf(
                SoftTeal.copy(alpha = 0.4f),
                SoftBlue.copy(alpha = 0.3f)
            )
        )
        else -> Brush.horizontalGradient(
            colors = listOf(
                GlassMedium,
                GlassLight
            )
        )
    }

    Card(
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = glassmorphismCardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .background(backgroundColor)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = genre,
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun VolumeSlider(
    modifier: Modifier = Modifier,
    volume: Float,
    onVolumeChange: (Float) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            Icons.Default.VolumeDown,
            contentDescription = "Volume Down",
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )

        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Slider(
                value = volume,
                onValueChange = onVolumeChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = SoftPurple,
                    inactiveTrackColor = GlassMedium
                )
            )
        }

        Icon(
            Icons.Default.VolumeUp,
            contentDescription = "Volume Up",
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun PlaylistCard(
    title: String,
    songCount: Int,
    duration: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = glassmorphismCardElevation()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .glassCard(alpha = 0.1f, cornerRadius = 16.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$songCount songs",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    IconButton(
                        onClick = onClick,
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                SoftGreen.copy(alpha = 0.3f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Play Playlist",
                            tint = SoftGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = duration,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Favorite",
                            tint = SoftPink,
                            modifier = Modifier.size(16.dp)
                        )
                        Icon(
                            Icons.Default.Download,
                            contentDescription = "Downloaded",
                            tint = SoftGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ArtistCard(
    name: String,
    albumCount: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .width(160.dp)
            .height(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = glassmorphismCardElevation()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            SoftPurple.copy(alpha = 0.3f),
                            SoftBlue.copy(alpha = 0.2f),
                            SoftTeal.copy(alpha = 0.1f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.2f),
                                    Color.White.copy(alpha = 0.1f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Artist",
                        tint = TextPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "$albumCount albums",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

// Additional glassmorphism utility functions
@Composable
fun glassmorphismCardElevation(
    defaultElevation: Dp = 8.dp,
    pressedElevation: Dp = 12.dp,
    focusedElevation: Dp = 10.dp,
    hoveredElevation: Dp = 10.dp,
    draggedElevation: Dp = 16.dp,
    disabledElevation: Dp = 0.dp
): CardElevation = CardDefaults.cardElevation(
    defaultElevation = defaultElevation,
    pressedElevation = pressedElevation,
    focusedElevation = focusedElevation,
    hoveredElevation = hoveredElevation,
    draggedElevation = draggedElevation,
    disabledElevation = disabledElevation
)

@Composable
fun GlassFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Transparent,
    content: @Composable () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = containerColor,
        elevation = FloatingActionButtonDefaults.elevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .glassCard(alpha = 0.15f, cornerRadius = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
fun GlassIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.glassCard(alpha = 0.12f, cornerRadius = 24.dp),
        enabled = enabled
    ) {
        content()
    }
}