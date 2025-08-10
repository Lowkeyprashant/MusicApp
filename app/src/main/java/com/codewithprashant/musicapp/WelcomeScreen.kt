// WelcomeScreen.kt - AnimatedVisibility Scope Fixed
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.codewithprashant.musicapp.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "welcome_animations")

    // Enhanced floating animation for headphones
    val headphonesScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "headphones_scale"
    )

    val headphonesRotation by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = EaseInOutSine),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "headphones_rotation"
    )

    // Sound waves animation
    val soundWaveScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "soundwave_scale"
    )

    // Particle animation
    val particleOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        ),
        label = "particle_offset"
    )

    // Enhanced background gradient
    val backgroundGradient = Brush.radialGradient(
        colors = listOf(
            DeepNavy,
            MidnightBlue,
            DarkCharcoal,
            Color(0xFF0A0A0A)
        ),
        radius = 1200f
    )

    LaunchedEffect(Unit) {
        isVisible = true
        delay(500)
        showContent = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        // Animated particle background
        repeat(12) { index ->
            val angle = (index * 30f + particleOffset)
            val radius = 150f + (index * 20f)
            val angleRadians = Math.toRadians(angle.toDouble())
            val x = radius * cos(angleRadians).toFloat()
            val y = radius * sin(angleRadians).toFloat()

            Box(
                modifier = Modifier
                    .offset(x.dp, y.dp)
                    .size((4 + index % 3 * 2).dp)
                    .background(
                        SoftPurple.copy(alpha = 0.3f - index * 0.02f),
                        CircleShape
                    )
                    .align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status bar spacer
            Spacer(modifier = Modifier.height(60.dp))

            // App branding section
            androidx.compose.animation.AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -100 },
                    animationSpec = tween(800, easing = EaseOutCubic)
                ) + fadeIn(animationSpec = tween(800)),
                label = "branding_animation"
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // App logo/icon placeholder
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        SoftPurple.copy(alpha = 0.8f),
                                        SoftBlue.copy(alpha = 0.6f)
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
                            Icons.Rounded.MusicNote,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Lumio",
                        color = SoftPurple,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Main illustration area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = showContent,
                    enter = scaleIn(
                        initialScale = 0.3f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(animationSpec = tween(1000)),
                    label = "illustration_animation"
                ) {
                    // Enhanced headphones illustration
                    Box(
                        modifier = Modifier.size(320.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Ambient glow effect
                        Box(
                            modifier = Modifier
                                .size(400.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            SoftPurple.copy(alpha = 0.15f),
                                            SoftBlue.copy(alpha = 0.1f),
                                            Color.Transparent
                                        ),
                                        radius = 300f
                                    ),
                                    CircleShape
                                )
                        )

                        // Sound wave decorations
                        repeat(6) { index ->
                            val waveAngle = index * 60f
                            val waveRadius = 160f + index * 15f
                            val waveAngleRadians = Math.toRadians(waveAngle.toDouble())
                            val waveX = waveRadius * cos(waveAngleRadians).toFloat()
                            val waveY = waveRadius * sin(waveAngleRadians).toFloat()

                            WelcomeSoundWave(
                                modifier = Modifier
                                    .offset(waveX.dp, waveY.dp)
                                    .scale(soundWaveScale * (0.6f + index * 0.1f))
                                    .graphicsLayer {
                                        alpha = 0.4f - index * 0.05f
                                    },
                                color = when (index % 3) {
                                    0 -> SoftPurple
                                    1 -> SoftBlue
                                    else -> SoftTeal
                                }
                            )
                        }

                        // Main headphones
                        WelcomeHeadphones(
                            modifier = Modifier
                                .scale(headphonesScale)
                                .graphicsLayer {
                                    rotationZ = headphonesRotation
                                }
                        )
                    }
                }
            }

            // Welcome content section
            androidx.compose.animation.AnimatedVisibility(
                visible = showContent,
                enter = slideInVertically(
                    initialOffsetY = { 200 },
                    animationSpec = tween(1000, delayMillis = 300, easing = EaseOutCubic)
                ) + fadeIn(animationSpec = tween(800, delayMillis = 300)),
                label = "content_animation"
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    // Main heading with background
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.08f),
                                            Color.White.copy(alpha = 0.12f),
                                            Color.White.copy(alpha = 0.08f)
                                        )
                                    )
                                )
                                .border(
                                    1.dp,
                                    Color.White.copy(alpha = 0.2f),
                                    RoundedCornerShape(24.dp)
                                )
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Welcome to Your",
                                    color = TextSecondary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = "Musical Universe",
                                    color = TextPrimary,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 38.sp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                Text(
                                    text = "Discover millions of songs, create personalized playlists, and enjoy high-quality audio streaming anywhere, anytime.",
                                    color = TextSecondary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 24.sp,
                                    modifier = Modifier.padding(top = 16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Enhanced CTA button
                    Button(
                        onClick = onGetStarted,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(32.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 12.dp,
                            pressedElevation = 16.dp
                        ),
                        border = BorderStroke(
                            1.dp,
                            SoftPurple.copy(alpha = 0.6f)
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            SoftPurple.copy(alpha = 0.8f),
                                            SoftBlue.copy(alpha = 0.6f),
                                            SoftTeal.copy(alpha = 0.4f)
                                        )
                                    ),
                                    RoundedCornerShape(32.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Rounded.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Text(
                                    text = "Start Your Journey",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Feature highlights
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        WelcomeFeature(
                            icon = Icons.Rounded.HighQuality,
                            text = "HD Audio",
                            color = SoftBlue
                        )
                        WelcomeFeature(
                            icon = Icons.Rounded.CloudOff,
                            text = "Offline Mode",
                            color = SoftGreen
                        )
                        WelcomeFeature(
                            icon = Icons.Rounded.Equalizer,
                            text = "Equalizer",
                            color = SoftPink
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun WelcomeHeadphones(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Enhanced headband with 3D effect
            Card(
                modifier = Modifier
                    .width(160.dp)
                    .height(24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    SoftPurple.copy(alpha = 0.9f),
                                    SoftBlue.copy(alpha = 0.8f),
                                    SoftTeal.copy(alpha = 0.7f)
                                )
                            )
                        )
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.3f),
                            RoundedCornerShape(12.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Enhanced ear cups
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.width(180.dp)
            ) {
                WelcomeEarCup()
                WelcomeEarCup()
            }
        }
    }
}

@Composable
fun WelcomeEarCup() {
    Card(
        modifier = Modifier.size(72.dp),
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Outer ring with gradient
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                SoftPurple.copy(alpha = 0.8f),
                                SoftBlue.copy(alpha = 0.6f),
                                SoftTeal.copy(alpha = 0.4f)
                            )
                        ),
                        CircleShape
                    )
                    .border(
                        2.dp,
                        Color.White.copy(alpha = 0.3f),
                        CircleShape
                    )
            )

            // Inner speaker with depth
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                DarkCharcoal,
                                DeepNavy,
                                MidnightBlue
                            )
                        ),
                        CircleShape
                    )
                    .border(
                        1.dp,
                        Color.White.copy(alpha = 0.2f),
                        CircleShape
                    )
            )

            // Speaker grille with mesh effect
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.8f),
                                Color.Black.copy(alpha = 0.9f)
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Speaker center dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            SoftPurple.copy(alpha = 0.6f),
                            CircleShape
                        )
                )
            }
        }
    }
}

@Composable
fun WelcomeSoundWave(
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height((16 + index * 8).dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                color.copy(alpha = 0.8f),
                                color.copy(alpha = 0.4f)
                            )
                        ),
                        RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

@Composable
fun WelcomeFeature(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = 0.3f),
                            color.copy(alpha = 0.1f)
                        )
                    ),
                    CircleShape
                )
                .border(
                    1.dp,
                    color.copy(alpha = 0.4f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = text,
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}