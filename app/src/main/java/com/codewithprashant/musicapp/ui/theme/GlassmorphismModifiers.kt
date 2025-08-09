// ui/theme/GlassmorphismModifiers.kt
package com.codewithprashant.musicapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Main glassmorphism effect modifier
fun Modifier.glassBackground(
    alpha: Float = 0.1f,
    cornerRadius: Dp = 16.dp
) = this
    .clip(RoundedCornerShape(cornerRadius))
    .background(Color.White.copy(alpha = alpha))
    .border(
        width = 1.dp,
        color = Color.White.copy(alpha = 0.2f),
        shape = RoundedCornerShape(cornerRadius)
    )

// Enhanced glass card with shadow and gradient
fun Modifier.glassCard(
    alpha: Float = 0.08f,
    cornerRadius: Dp = 20.dp,
    shadowElevation: Dp = 8.dp
) = this
    .shadow(
        elevation = shadowElevation,
        shape = RoundedCornerShape(cornerRadius),
        ambientColor = Color.Black.copy(alpha = 0.3f),
        spotColor = Color.Black.copy(alpha = 0.3f)
    )
    .clip(RoundedCornerShape(cornerRadius))
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = alpha + 0.02f),
                Color.White.copy(alpha = alpha)
            )
        )
    )
    .border(
        width = 1.dp,
        color = Color.White.copy(alpha = 0.15f),
        shape = RoundedCornerShape(cornerRadius)
    )

// Soft gradient background modifier
fun Modifier.softGradientBackground(
    colors: List<Color> = listOf(
        SoftPurple.copy(alpha = 0.6f),
        SoftBlue.copy(alpha = 0.4f)
    ),
    alpha: Float = 0.6f
) = this.background(
    brush = Brush.horizontalGradient(
        colors = colors.map { it.copy(alpha = alpha) }
    )
)

// Premium glow effect
fun Modifier.premiumGlow(
    glowColor: Color = SoftPurple,
    cornerRadius: Dp = 16.dp
) = this
    .shadow(
        elevation = 12.dp,
        shape = RoundedCornerShape(cornerRadius),
        ambientColor = glowColor.copy(alpha = 0.3f),
        spotColor = glowColor.copy(alpha = 0.5f)
    )

// Card elevation for glassmorphism
@Composable
fun glassmorphismCardElevation(): CardElevation = CardDefaults.cardElevation(
    defaultElevation = 8.dp,
    pressedElevation = 12.dp,
    focusedElevation = 10.dp,
    hoveredElevation = 10.dp,
    draggedElevation = 16.dp,
    disabledElevation = 0.dp
)

// Preset Glass Styles
object GlassStyles {
    @Composable
    fun lightGlass() = Modifier.glassCard(alpha = 0.1f, cornerRadius = 16.dp)

    @Composable
    fun mediumGlass() = Modifier.glassCard(alpha = 0.15f, cornerRadius = 20.dp)

    @Composable
    fun darkGlass() = Modifier.glassCard(alpha = 0.05f, cornerRadius = 24.dp)

    @Composable
    fun glowingGlass(color: Color = SoftPurple) = Modifier
        .glassCard(alpha = 0.12f, cornerRadius = 18.dp)
        .premiumGlow(glowColor = color)
}

// Gradient definitions for easy reuse
object GradientDefaults {
    val MainGradient = Brush.verticalGradient(
        colors = listOf(DeepNavy, MidnightBlue, DarkCharcoal)
    )

    val CardGradient = Brush.horizontalGradient(
        colors = listOf(GlassMedium, GlassLight)
    )

    val ButtonGradient = Brush.radialGradient(
        colors = listOf(SoftPurple, SoftBlue)
    )

    val ProgressGradient = Brush.horizontalGradient(
        colors = listOf(SoftPurple, SoftPink)
    )

    val GlassGradient = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.15f),
            Color.White.copy(alpha = 0.05f)
        )
    )

    // Genre-specific gradients with reduced saturation
    val RockGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFDC2626).copy(alpha = 0.4f),
            Color(0xFFEF4444).copy(alpha = 0.3f)
        )
    )

    val PopGradient = Brush.horizontalGradient(
        colors = listOf(
            SoftPink.copy(alpha = 0.4f),
            SoftPurple.copy(alpha = 0.3f)
        )
    )

    val ElectronicGradient = Brush.horizontalGradient(
        colors = listOf(
            SoftTeal.copy(alpha = 0.4f),
            SoftBlue.copy(alpha = 0.3f)
        )
    )
}