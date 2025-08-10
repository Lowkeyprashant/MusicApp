// ThemeExtensions.kt - Essential Modifier Extensions Only
package com.codewithprashant.musicapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Creates a glassmorphism effect with background blur and transparency
 */
fun Modifier.glassCard(
    alpha: Float = 0.1f,
    cornerRadius: Dp = 16.dp,
    borderWidth: Dp = 1.dp,
    borderAlpha: Float = 0.2f
): Modifier = this
    .clip(RoundedCornerShape(cornerRadius))
    .background(
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = alpha),
                Color.White.copy(alpha = alpha * 0.7f)
            )
        )
    )
    .border(
        width = borderWidth,
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = borderAlpha),
                Color.White.copy(alpha = borderAlpha * 0.5f)
            )
        ),
        shape = RoundedCornerShape(cornerRadius)
    )

/**
 * Adds a premium glow effect around the element
 */
fun Modifier.premiumGlow(
    glowColor: Color = Color(0xFF9D4EDD), // SoftPurple
    glowRadius: Dp = 8.dp,
    alpha: Float = 0.6f
): Modifier = this
    .background(
        Brush.radialGradient(
            colors = listOf(
                glowColor.copy(alpha = alpha * 0.3f),
                glowColor.copy(alpha = alpha * 0.1f),
                Color.Transparent
            ),
            radius = glowRadius.value * 3
        ),
        RoundedCornerShape(glowRadius)
    )