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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithprashant.musicapp.ui.theme.*
import kotlin.random.Random

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
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GradientDefaults.MainGradient)
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
                        brush = GradientDefaults.ProgressGradient,
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
                color = ProgressBarInactive,
                radius = size.minDimension / 2,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )

            drawArc(
                color = ProgressGradientStart,
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
                    .background(GradientDefaults.ButtonGradient, CircleShape),
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
        "rock" -> GradientDefaults.RockGradient
        "pop" -> GradientDefaults.PopGradient
        "electronic" -> GradientDefaults.ElectronicGradient
        else -> GradientDefaults.CardGradient
    }

    Card(
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
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

        Slider(
            value = volume,
            onValueChange = onVolumeChange,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = ProgressGradientStart,
                activeTrackColor = ProgressGradientStart,
                inactiveTrackColor = ProgressBarInactive
            )
        )

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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GradientDefaults.CardGradient)
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

                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Play Playlist",
                        tint = AccentGreen,
                        modifier = Modifier.size(24.dp)
                    )
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
                            tint = AccentPink,
                            modifier = Modifier.size(16.dp)
                        )
                        Icon(
                            Icons.Default.Download,
                            contentDescription = "Downloaded",
                            tint = AccentGreen,
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GradientDefaults.MainGradient)
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
                            brush = GradientDefaults.GlassGradient,
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