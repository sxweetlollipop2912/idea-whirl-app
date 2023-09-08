package com.example.ideawhirl.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class DrawingColor(val light: Color, val dark: Color, val id: Int) {
    WHITE(Color(0xFFFFFFFF), Color(0xFFD1D2D2), id = 0),
    BLACK(Color(0xFF000000), Color(0xFF000000), id = 1),
    RED(Color(0xFFFF1F31), Color(0xFFBA1E2B), id = 2),
    ORANGE(Color(0xFFFF8B00), Color(0xFFDD7A04), id = 3),
    YELLOW(Color(0xFFFFD200), Color(0xFFE8C003), id = 4),
    GREEN(Color(0xFF00C344), Color(0xFF05A23C), id = 5),
    BLUE(Color(0xFF456EFE), Color(0xFF4166E8), id = 6),
    PURPLE(Color(0xFF8C5AFF), Color(0xFF704BC7), id = 7),
    PINK(Color(0xFFFF7AAD), Color(0xFFE8719F), id = 8),
    BROWN(Color(0xFFAA7942), Color(0xFF7F5D37), id = 9);

    companion object  {
        @Composable
        fun toColorList(): List<Color> {
            val enums = values().toList().sortedBy { it.id }
            return if (isSystemInDarkTheme()) {
                enums.map { it.dark }
            } else {
                enums.map { it.light }
            }
        }
    }
}