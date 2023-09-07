package com.example.ideawhirl.ui.notedraw

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NoteDrawRoute(
    noteDrawViewModel: NoteDrawViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val availableColors = noteDrawViewModel.availableColors
    val palette by noteDrawViewModel.palette.collectAsStateWithLifecycle()
    val drawingData by noteDrawViewModel.drawingData.collectAsStateWithLifecycle()
    val drawingConfig by noteDrawViewModel.drawingConfig.collectAsStateWithLifecycle()

    NoteDrawScreen(
        availableColors = availableColors,
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
