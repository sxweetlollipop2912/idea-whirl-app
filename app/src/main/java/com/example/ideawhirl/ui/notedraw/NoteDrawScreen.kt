package com.example.ideawhirl.ui.notedraw

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ideawhirl.ui.components.drawing_board.DrawingBoard
import com.example.ideawhirl.ui.components.drawing_board.DrawingConfig
import com.example.ideawhirl.ui.components.drawing_board.DrawingData
import com.example.ideawhirl.ui.theme.NotePalette
import com.example.ideawhirl.ui.theme.NoteTheme

@Composable
fun NoteDrawScreen(
    availableColors: List<Color>,
    palette: NotePalette,
    drawingData: DrawingData,
    onUpdateDrawingData: (DrawingData) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    drawingConfig: DrawingConfig = DrawingConfig(),
    onUpdateDrawingConfig: (DrawingConfig) -> Unit,
    modifier: Modifier = Modifier,
) {
    NoteTheme(palette = palette) {
        DrawingBoard(
            availableStrokeColors = availableColors,
            backgroundColor = palette.background,
            drawingData = drawingData,
            onUpdateDrawingData = onUpdateDrawingData,
            onSave = onSave,
            onBack = onBack,
            drawingConfig = drawingConfig,
            onUpdateDrawingConfig = onUpdateDrawingConfig,
        )
    }
}