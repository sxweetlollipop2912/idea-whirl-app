package com.example.ideawhirl.ui.notedraw

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ideawhirl.ui.noteDrawdraw.NoteDrawViewModel

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
        backgroundColor = palette.background,
        drawingData = drawingData,
        onUpdateDrawingData = noteDrawViewModel::onUpdateDrawingData,
        onSave = noteDrawViewModel::onSave,
        drawingConfig = drawingConfig,
        onUpdateDrawingConfig = noteDrawViewModel::onUpdateDrawingConfig,
    )
}
