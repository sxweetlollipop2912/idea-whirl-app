package com.example.ideawhirl.ui.components.drawing_board

enum class Mode {
    DRAWING,
    ERASING,
}

data class DrawingConfig(
    val strokeWidth: StrokeWidth = StrokeWidth.Normal,
    val strokeColorIndex: Int = 0,
    val eraserWidth: EraserWidth = EraserWidth.Normal,
    val mode: Mode = Mode.DRAWING,
)