// ui/theme/Color.kt
package com.codewithprashant.musicapp.ui.theme

import androidx.compose.ui.graphics.Color

// Dark Premium Background Colors - Much darker for premium feel
val DeepNavy = Color(0xFF0A0A15)
val DarkCharcoal = Color(0xFF12121C)
val DeepPurple = Color(0xFF1A1A2E)
val MidnightBlue = Color(0xFF16213E)
val DarkestBlue = Color(0xFF0F1419)

// Glass Panel Colors (with transparency for glassmorphism)
val GlassLight = Color(0x1AFFFFFF)        // 10% white
val GlassMedium = Color(0x26FFFFFF)       // 15% white
val GlassDark = Color(0x0DFFFFFF)         // 5% white
val GlassHighlight = Color(0x33FFFFFF)    // 20% white

// Softer Accent Colors (reduced saturation from your original vibrant colors)
val SoftPurple = Color(0xFF7C3AED)        // Reduced from #9333EA
val SoftBlue = Color(0xFF4F46E5)          // Reduced from #6366F1
val SoftPink = Color(0xFFDB2777)          // Reduced from #EC4899
val SoftTeal = Color(0xFF0891B2)          // Reduced from #06B6D4
val SoftGreen = Color(0xFF059669)         // Reduced from #10B981

// Subtle Gradient Colors
val GradientStart = Color(0xFF1E1B4B)
val GradientMid = Color(0xFF312E81)
val GradientEnd = Color(0xFF1F2937)

// Text Colors with proper contrast for dark backgrounds
val TextPrimary = Color(0xFFFFFFFF)       // Pure white for primary text
val TextSecondary = Color(0xB3FFFFFF)     // 70% white for secondary text
val TextTertiary = Color(0x80FFFFFF)      // 50% white for tertiary text
val TextDisabled = Color(0x4DFFFFFF)      // 30% white for disabled text

// Status Colors
val SuccessGreen = Color(0xFF10B981)
val WarningOrange = Color(0xFFF59E0B)
val ErrorRed = Color(0xFFEF4444)

// Legacy colors (keeping for backward compatibility)
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Additional glassmorphism specific colors
val BackgroundPrimary = DeepNavy
val BackgroundSecondary = DarkCharcoal
val SurfaceGlass = GlassMedium
val OnSurfaceGlass = TextPrimary
val OutlineGlass = GlassLight

// Progress and accent colors
val ProgressGradientStart = SoftPurple
val ProgressGradientEnd = SoftPink
val ProgressBarInactive = GlassMedium
val AccentGreen = SoftGreen
val AccentPink = SoftPink