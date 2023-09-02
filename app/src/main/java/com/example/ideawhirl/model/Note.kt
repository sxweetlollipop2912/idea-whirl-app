package com.example.ideawhirl.model

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.compose.note_dark_blue
import com.example.compose.note_dark_blue_background
import com.example.compose.note_dark_blue_variant
import com.example.compose.note_dark_green
import com.example.compose.note_dark_green_background
import com.example.compose.note_dark_green_variant
import com.example.compose.note_dark_orange
import com.example.compose.note_dark_orange_background
import com.example.compose.note_dark_orange_variant
import com.example.compose.note_dark_pink
import com.example.compose.note_dark_pink_background
import com.example.compose.note_dark_pink_variant
import com.example.compose.note_light_blue
import com.example.compose.note_light_blue_background
import com.example.compose.note_light_blue_variant
import com.example.compose.note_light_green
import com.example.compose.note_light_green_background
import com.example.compose.note_light_green_variant
import com.example.compose.note_light_orange
import com.example.compose.note_light_orange_background
import com.example.compose.note_light_orange_variant
import com.example.compose.note_light_pink
import com.example.compose.note_light_pink_background
import com.example.compose.note_light_pink_variant
import java.util.Date

enum class NotePalette(
    private val light: Color,
    private val lightVariant: Color,
    private val lightBackground: Color,
    private val dark: Color,
    private val darkVariant: Color,
    private val darkBackground: Color,
    val id: Int) {
    PALETTE_1(
        note_light_pink,
        note_light_pink_variant,
        note_light_pink_background,
        note_dark_pink,
        note_dark_pink_variant,
        note_dark_pink_background,
        1),
    PALETTE_2(
        note_light_blue,
        note_light_blue_variant,
        note_light_blue_background,
        note_dark_blue,
        note_dark_blue_variant,
        note_dark_blue_background,
        2),
    PALETTE_3(
        note_light_green,
        note_light_green_variant,
        note_light_green_background,
        note_dark_green,
        note_dark_green_variant,
        note_dark_green_background,
        3),
    PALETTE_4(
        note_light_orange,
        note_light_orange_variant,
        note_light_orange_background,
        note_dark_orange,
        note_dark_orange_variant,
        note_dark_orange_background,
        4);

    val main: Color
        @Composable
        get() {
            return if (isSystemInDarkTheme()) {
                dark
            } else {
                light
            }
        }
    val variant: Color
        @Composable
        get() {
            return if (isSystemInDarkTheme()) {
                darkVariant
            } else {
                lightVariant
            }
        }
    val background: Color
        @Composable
        get() {
            return if (isSystemInDarkTheme()) {
                darkBackground
            } else {
                lightBackground
            }
        }

    companion object {
        fun random() = values().toList().random()
        fun fromId(id: Int) = values().first { it.id == id }
    }
}

data class Note(
    val name: String,
    val detail: String,
    val tags: List<String>,
    val uid: Int = 0,
    val createdAt: Date? = null,
    val palette: NotePalette = NotePalette.random(),
)
