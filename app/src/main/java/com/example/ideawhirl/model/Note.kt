package com.example.ideawhirl.model

import android.content.Context
import com.example.ideawhirl.components.drawing_board.DrawingData
import kotlinx.serialization.json.Json
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.ideawhirl.ui.theme.note_dark_blue
import com.example.ideawhirl.ui.theme.note_dark_blue_background
import com.example.ideawhirl.ui.theme.note_dark_blue_variant
import com.example.ideawhirl.ui.theme.note_dark_green
import com.example.ideawhirl.ui.theme.note_dark_green_background
import com.example.ideawhirl.ui.theme.note_dark_green_variant
import com.example.ideawhirl.ui.theme.note_dark_on_custom
import com.example.ideawhirl.ui.theme.note_dark_orange
import com.example.ideawhirl.ui.theme.note_dark_orange_background
import com.example.ideawhirl.ui.theme.note_dark_orange_variant
import com.example.ideawhirl.ui.theme.note_dark_pink
import com.example.ideawhirl.ui.theme.note_dark_pink_background
import com.example.ideawhirl.ui.theme.note_dark_pink_variant
import com.example.ideawhirl.ui.theme.note_dark_purple
import com.example.ideawhirl.ui.theme.note_dark_purple_background
import com.example.ideawhirl.ui.theme.note_dark_purple_variant
import com.example.ideawhirl.ui.theme.note_light_blue
import com.example.ideawhirl.ui.theme.note_light_blue_background
import com.example.ideawhirl.ui.theme.note_light_blue_variant
import com.example.ideawhirl.ui.theme.note_light_green
import com.example.ideawhirl.ui.theme.note_light_green_background
import com.example.ideawhirl.ui.theme.note_light_green_variant
import com.example.ideawhirl.ui.theme.note_light_on_custom
import com.example.ideawhirl.ui.theme.note_light_orange
import com.example.ideawhirl.ui.theme.note_light_orange_background
import com.example.ideawhirl.ui.theme.note_light_orange_variant
import com.example.ideawhirl.ui.theme.note_light_pink
import com.example.ideawhirl.ui.theme.note_light_pink_background
import com.example.ideawhirl.ui.theme.note_light_pink_variant
import com.example.ideawhirl.ui.theme.note_light_purple
import com.example.ideawhirl.ui.theme.note_light_purple_background
import com.example.ideawhirl.ui.theme.note_light_purple_variant
import kotlinx.serialization.encodeToString
import java.io.File
import java.nio.file.Files
import java.util.Date

enum class NotePalette(
    private val light: Color,
    private val lightVariant: Color,
    private val lightBackground: Color,
    private val dark: Color,
    private val darkVariant: Color,
    private val darkBackground: Color,
    val id: Int) {
    PALETTE_0(
        Color(0), Color(0), Color(0), Color(0), Color(0), Color(0),
        0),
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

    val buttonContent: Color
        @Composable
        get() {
            return if (id == 0) {
                MaterialTheme.colorScheme.onBackground
            } else if (isSystemInDarkTheme()) {
                MaterialTheme.colorScheme.onBackground
            } else {
                MaterialTheme.colorScheme.onBackground
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
    val tags: Set<String>,
    val uid: Int = 0,
    val createdAt: Date? = null,
    val palette: NotePalette = NotePalette.random(),
    val drawingData: DrawingData = DrawingData.emptyData(),
) {
    private var _drawingData: DrawingData? = null

    var drawingData: DrawingData
        get() {
            if (_drawingData == null) {
                return loadOrCreateDrawingData()
            }
            return _drawingData
                ?: throw AssertionError("Set to null after initialized by another thread")
        }
        set(value) {
            _drawingData = value
        }

    private val filename = "drawing_$uid.data"
    private fun loadOrCreateDrawingData(): DrawingData {
        try {
            context.openFileInput(filename).bufferedReader().useLines { lines ->
                val content = lines.fold("") { content, text ->
                    content.plus(text)
                }
                return Json.decodeFromString(content)
            }
        } catch (e: Throwable) {
            return DrawingData(listOf())
        }
    }

    fun saveDrawingPath() {
        if (_drawingData == null) {
            throw AssertionError("Drawing data is not initialized.")
        }

        if (uid == 0) {
            throw AssertionError("Note must be fetched from database to save.")
        }
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(Json.encodeToString(drawingData).toByteArray())
        }
    }

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
