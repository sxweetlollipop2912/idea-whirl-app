package com.example.ideawhirl.model

import com.example.ideawhirl.ui.components.drawing_board.DrawingData
import com.example.ideawhirl.ui.theme.NotePalette
import java.util.Date

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
