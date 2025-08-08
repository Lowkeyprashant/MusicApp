package com.codewithprashant.musicapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = TextPrimary,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = TextPrimary,
    secondary = Secondary,
    onSecondary = TextPrimary,
    secondaryContainer = SecondaryVariant,
    onSecondaryContainer = TextPrimary,
    tertiary = AccentPink,
    onTertiary = TextPrimary,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = CardDark,
    onSurfaceVariant = TextSecondary,
    outline = TextTertiary,
    outlineVariant = TextTertiary,
    error = AccentRed,
    onError = TextPrimary,
    errorContainer = AccentRed,
    onErrorContainer = TextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = TextPrimaryLight,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = TextPrimaryLight,
    secondary = Secondary,
    onSecondary = TextPrimaryLight,
    secondaryContainer = SecondaryVariant,
    onSecondaryContainer = TextPrimaryLight,
    tertiary = AccentPink,
    onTertiary = TextPrimaryLight,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = CardLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = TextTertiaryLight,
    outlineVariant = TextTertiaryLight,
    error = AccentRed,
    onError = TextPrimaryLight,
    errorContainer = AccentRed,
    onErrorContainer = TextPrimaryLight
)

object GradientDefaults {
    val MainGradient = Brush.verticalGradient(
        colors = listOf(GradientStart, GradientMiddle, GradientEnd)
    )

    val PlayerGradient = Brush.verticalGradient(
        colors = listOf(PlayerGradientStart, PlayerGradientMiddle, PlayerGradientEnd)
    )

    val CardGradient = Brush.verticalGradient(
        colors = listOf(CardGradientStart, CardGradientEnd)
    )

    val ButtonGradient = Brush.horizontalGradient(
        colors = listOf(ButtonGradientStart, ButtonGradientEnd)
    )

    val ProgressGradient = Brush.horizontalGradient(
        colors = listOf(ProgressGradientStart, ProgressGradientEnd)
    )

    val OverlayGradient = Brush.verticalGradient(
        colors = listOf(OverlayGradientStart, OverlayGradientMiddle, OverlayGradientEnd)
    )

    val ShimmerGradient = Brush.horizontalGradient(
        colors = listOf(ShimmerGradientStart, ShimmerGradientMiddle, ShimmerGradientEnd)
    )

    val GlassGradient = Brush.verticalGradient(
        colors = listOf(GlassGradientStart, GlassGradientEnd)
    )

    val RockGradient = Brush.horizontalGradient(
        colors = listOf(RockGradientStart, RockGradientEnd)
    )

    val PopGradient = Brush.horizontalGradient(
        colors = listOf(PopGradientStart, PopGradientEnd)
    )

    val ElectronicGradient = Brush.horizontalGradient(
        colors = listOf(ElectronicGradientStart, ElectronicGradientEnd)
    )
}

@Composable
fun MusicAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}