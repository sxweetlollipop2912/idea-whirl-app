package com.example.ideawhirl.ui.notedraw

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ideawhirl.ui.theme.DrawingColor

@Composable
fun NoteDrawRoute(
    noteDrawViewModel: NoteDrawViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette by noteDrawViewModel.palette.collectAsStateWithLifecycle()
    val drawingData by noteDrawViewModel.drawingData.collectAsStateWithLifecycle()
    val drawingConfig by noteDrawViewModel.drawingConfig.collectAsStateWithLifecycle()

    NoteDrawScreen(
        availableColors = DrawingColor.toColorList(),
        palette = palette,
        drawingData = drawingData,
        onUpdateDrawingData = noteDrawViewModel::onUpdateDrawingData,
        onSave = noteDrawViewModel::onSave,
        onBack = onBack,
        drawingConfig = drawingConfig,
        onUpdateDrawingConfig = noteDrawViewModel::onUpdateDrawingConfig,
        modifier = modifier,
    )
}
