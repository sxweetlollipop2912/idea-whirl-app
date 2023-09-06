package com.example.ideawhirl.ui.notedraw

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.ideawhirl.ui.components.drawing_board.DrawingBoard
import com.example.ideawhirl.ui.components.drawing_board.DrawingConfig
import com.example.ideawhirl.ui.components.drawing_board.DrawingData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDrawScreen(
    availableColors: List<Color>,
    backgroundColor: Color,
    drawingData: DrawingData,
    onUpdateDrawingData: (DrawingData) -> Unit,
    onSave: () -> Unit,
    drawingConfig: DrawingConfig = DrawingConfig(),
    onUpdateDrawingConfig: (DrawingConfig) -> Unit,
) {
    DrawingBoard(
        availableStrokeColors = availableColors,
        backgroundColor = backgroundColor,
        drawingData = drawingData,
        onUpdateDrawingData = onUpdateDrawingData,
        onSave = onSave,
        drawingConfig = drawingConfig,
        onUpdateDrawingConfig = onUpdateDrawingConfig,
    )
}