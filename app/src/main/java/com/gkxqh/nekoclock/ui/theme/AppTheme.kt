package com.gkxqh.nekoclock.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

object AppTheme {
    val PresetColors = listOf(
        Color(0xFFE0E0E0), // Minimal White
        Color(0xFF4CAF50), // Emerald
        Color(0xFF00E5FF), // Neon Blue
        Color(0xFFFF5722), // Sunset
        Color(0xFFF48FB1), // Sakura
        Color(0xFFFFD700), // Gold
        Color(0xFF66CCFF)  // Sky Blue
    )

    fun getFontFamily(name: String): FontFamily = when (name) {
        "Monospace" -> FontFamily.Monospace
        "Serif" -> FontFamily.Serif
        "SansSerif" -> FontFamily.SansSerif
        "Cursive" -> FontFamily.Cursive
        else -> FontFamily.Monospace
    }
}
