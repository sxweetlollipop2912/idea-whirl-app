package com.example.ideawhirl.model

import com.example.ideawhirl.ui.components.drawing_board.DrawingData
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.ideawhirl.ui.theme.note_dark_blue
import com.example.ideawhirl.ui.theme.note_dark_blue_background
import com.example.ideawhirl.ui.theme.note_dark_blue_on_emphasis
import com.example.ideawhirl.ui.theme.note_dark_blue_variant
import com.example.ideawhirl.ui.theme.note_dark_green
import com.example.ideawhirl.ui.theme.note_dark_green_background
import com.example.ideawhirl.ui.theme.note_dark_green_on_emphasis
import com.example.ideawhirl.ui.theme.note_dark_green_variant
import com.example.ideawhirl.ui.theme.note_dark_on_custom
import com.example.ideawhirl.ui.theme.note_dark_orange
import com.example.ideawhirl.ui.theme.note_dark_orange_background
import com.example.ideawhirl.ui.theme.note_dark_orange_on_emphasis
import com.example.ideawhirl.ui.theme.note_dark_orange_variant
import com.example.ideawhirl.ui.theme.note_dark_pink
import com.example.ideawhirl.ui.theme.note_dark_pink_background
import com.example.ideawhirl.ui.theme.note_dark_pink_on_emphasis
import com.example.ideawhirl.ui.theme.note_dark_pink_variant
import com.example.ideawhirl.ui.theme.note_dark_purple
import com.example.ideawhirl.ui.theme.note_dark_purple_background
import com.example.ideawhirl.ui.theme.note_dark_purple_on_emphasis
import com.example.ideawhirl.ui.theme.note_dark_purple_variant
import com.example.ideawhirl.ui.theme.note_light_blue
import com.example.ideawhirl.ui.theme.note_light_blue_background
import com.example.ideawhirl.ui.theme.note_light_blue_on_emphasis
import com.example.ideawhirl.ui.theme.note_light_blue_variant
import com.example.ideawhirl.ui.theme.note_light_green
import com.example.ideawhirl.ui.theme.note_light_green_background
import com.example.ideawhirl.ui.theme.note_light_green_on_emphasis
import com.example.ideawhirl.ui.theme.note_light_green_variant
import com.example.ideawhirl.ui.theme.note_light_on_custom
import com.example.ideawhirl.ui.theme.note_light_orange
import com.example.ideawhirl.ui.theme.note_light_orange_background
import com.example.ideawhirl.ui.theme.note_light_orange_on_emphasis
import com.example.ideawhirl.ui.theme.note_light_orange_variant
import com.example.ideawhirl.ui.theme.note_light_pink
import com.example.ideawhirl.ui.theme.note_light_pink_background
import com.example.ideawhirl.ui.theme.note_light_pink_on_emphasis
import com.example.ideawhirl.ui.theme.note_light_pink_variant
import com.example.ideawhirl.ui.theme.note_light_purple
import com.example.ideawhirl.ui.theme.note_light_purple_background
import com.example.ideawhirl.ui.theme.note_light_purple_on_emphasis
import com.example.ideawhirl.ui.theme.note_light_purple_variant
import java.util.Date

enum class NotePalette(
    private val light: Color,
    private val lightVariant: Color,
    private val lightBackground: Color,
    private val lightOnEmphasis: Color,
    private val dark: Color,
    private val darkVariant: Color,
    private val darkBackground: Color,
    private val darkOnEmphasis: Color,
    val id: Int) {
    PALETTE_0(
        Color(0), Color(0), Color(0), Color(0), Color(0), Color(0), Color(0), Color(0),
        0),
    PALETTE_1(
        note_light_pink,
        note_light_pink_variant,
        note_light_pink_background,
        note_light_pink_on_emphasis,
        note_dark_pink,
        note_dark_pink_variant,
        note_dark_pink_background,
        note_dark_pink_on_emphasis,
        1),
    PALETTE_2(
        note_light_blue,
        note_light_blue_variant,
        note_light_blue_background,
        note_light_blue_on_emphasis,
        note_dark_blue,
        note_dark_blue_variant,
        note_dark_blue_background,
        note_dark_blue_on_emphasis,
        2),
    PALETTE_3(
        note_light_green,
        note_light_green_variant,
        note_light_green_background,
        note_light_green_on_emphasis,
        note_dark_green,
        note_dark_green_variant,
        note_dark_green_background,
        note_dark_green_on_emphasis,
        3),
    PALETTE_4(
        note_light_orange,
        note_light_orange_variant,
        note_light_orange_background,
        note_light_orange_on_emphasis,
        note_dark_orange,
        note_dark_orange_variant,
        note_dark_orange_background,
        note_dark_orange_on_emphasis,
        4),
    PALETTE_5(
        note_light_purple,
        note_light_purple_variant,
        note_light_purple_background,
        note_light_purple_on_emphasis,
        note_dark_purple,
        note_dark_purple_variant,
        note_dark_purple_background,
        note_dark_purple_on_emphasis,
        5);

    val main: Color
        @Composable
        get() {
            return if (id == 0) {
                MaterialTheme.colorScheme.primary
            } else if (isSystemInDarkTheme()) {
                dark
            } else {
                light
            }
        }
    val variant: Color
        @Composable
        get() {
            return if (id == 0) {
                MaterialTheme.colorScheme.primaryContainer
            } else if (isSystemInDarkTheme()) {
                darkVariant
            } else {
                lightVariant
            }
        }
    val background: Color
        @Composable
        get() {
            return if (id == 0) {
                MaterialTheme.colorScheme.background
            } else if (isSystemInDarkTheme()) {
                darkBackground
            } else {
                lightBackground
            }
        }
    val onVariant: Color
        @Composable
        get() {
            return if (id == 0) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else if (isSystemInDarkTheme()) {
                note_dark_on_custom
            } else {
                note_light_on_custom
            }
        }
    val onBackground: Color
        @Composable
        get() {
            return MaterialTheme.colorScheme.onBackground
        }

    val onEmphasis: Color
        @Composable
        get() {
            return if (id == 0) {
                MaterialTheme.colorScheme.onBackground
            } else if (isSystemInDarkTheme()) {
                darkOnEmphasis
            } else {
                lightOnEmphasis
            }
        }

    companion object {
        fun random() = values().filter { it.id != 0 }.toList().random()
        fun fromId(id: Int) = values().first { it.id == id }
    }
}

data class Note(
    val name: String,
    val detail: String,
    val tags: Set<String>,
    val uid: Int = 0,
    val createdAt: Date? = null,
    val palette: NotePalette = NotePalette.random(),
    val drawingData: DrawingData = DrawingData.emptyData(),
) {
    companion object {
        fun dummy() = Note(
            name = "",
            detail = "",
            tags = emptySet(),
            uid = 0,
            createdAt = null,
            palette = NotePalette.PALETTE_0,
            drawingData = DrawingData.emptyData(),
        )
    }

}
