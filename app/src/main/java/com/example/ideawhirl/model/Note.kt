package com.example.ideawhirl.model

import androidx.compose.ui.graphics.Color
import java.util.Date

enum class NotePalette(val light: Color, val lightVariant: Color, val dark: Color, val darkVariant: Color, val id: Int) {
    PALETTE_1(Color(0xFFD6E4F7), Color(0xFFD6E4F7), Color(0xFFD6E4F7), Color(0xFFD6E4F7), 1),
    PALETTE_2(Color(0xFFD6E4F7), Color(0xFFD6E4F7), Color(0xFFD6E4F7), Color(0xFFD6E4F7), 2),
    PALETTE_3(Color(0xFFD6E4F7), Color(0xFFD6E4F7), Color(0xFFD6E4F7), Color(0xFFD6E4F7), 3),
    PALETTE_4(Color(0xFFD6E4F7), Color(0xFFD6E4F7), Color(0xFFD6E4F7), Color(0xFFD6E4F7), 4),
    PALETTE_5(Color(0xFFD6E4F7), Color(0xFFD6E4F7), Color(0xFFD6E4F7), Color(0xFFD6E4F7), 5);

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
