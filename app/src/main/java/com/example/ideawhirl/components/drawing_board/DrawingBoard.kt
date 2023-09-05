package com.example.ideawhirl.components.drawing_board

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

enum class MotionEvent {
    Idle, Down, Move, Up
}

const val OFFSET_TOLERANCE = 2

@Composable
fun DrawingBoard(
    availableStrokeColors: List<Color>, backgroundColor: Color,
    drawingData: DrawingData,
    onUpdateDrawingData: (DrawingData) -> Unit,
    onSave: (DrawingData) -> Unit,
    drawingConfig: DrawingConfig,
    onUpdateDrawingConfig: (DrawingConfig) -> Unit,
) {
    if (availableStrokeColors.size != 10) {
        throw (Throwable("Invalid arguments, colors list must have exactly 10 colors."))
    }
    var showEraserWidthChooser by remember { mutableStateOf(false) }
    var showColorChooser by remember { mutableStateOf(false) }
    var showStrokeWidthChooser by remember { mutableStateOf(false) }
    var inViewMode by remember { mutableStateOf(false) }

    if (!inViewMode) {
        DrawingSurface(
            drawingData.paths,
            { newPaths -> onUpdateDrawingData(DrawingData(newPaths)) },
            availableStrokeColors,
            backgroundColor,
            drawingConfig = drawingConfig,
        )
    } else {
        DisplayBoard(
            drawingData.paths,
            { inViewMode = false },
            availableStrokeColors,
            backgroundColor
        )
    }
    // A layer in front of Canvas to capture user input while toolbar being expanded
    if (showColorChooser || showStrokeWidthChooser) {
        Box(modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures {
                    showStrokeWidthChooser = false
                    showColorChooser = false
                }
            })
    }
    Row() {
        if (!inViewMode) {
            ColorBox(
                expanded = showColorChooser,
                onExpanded = { showColorChooser = true },
                onCollapsed = { showColorChooser = false },
                currentStrokeColorIndex = drawingConfig.strokeColorIndex,
                onStrokeColorChanged = { index ->
                    onUpdateDrawingConfig(drawingConfig.copy(strokeColorIndex = index))
                },
                availableStrokeColors,
            )
            when (drawingConfig.mode) {
                Mode.DRAWING -> {
                    StrokeWidthBox(
                        strokeColor = availableStrokeColors[drawingConfig.strokeColorIndex],
                        expanded = showStrokeWidthChooser,
                        onExpanded = { showStrokeWidthChooser = true },
                        onCollapsed = { showStrokeWidthChooser = false },
                        currentStrokeWidth = drawingConfig.strokeWidth,
                        onStrokeWidthChanged = { width ->
                            onUpdateDrawingConfig(drawingConfig.copy(strokeWidth = width))
                        }
                    )
                }

                Mode.ERASING -> {
                    EraserWidthBox(
                        expanded = showEraserWidthChooser,
                        onExpanded = { showEraserWidthChooser = true },
                        onCollapsed = { showEraserWidthChooser = false },
                        currentEraserWidth = (drawingConfig).eraserWidth,
                        onEraserWidthChanged = { newWidth ->
                            onUpdateDrawingConfig(drawingConfig.copy(eraserWidth = newWidth))
                        },
                    )
                }
            }
            IconToggleButton(
                checked = (drawingConfig.mode == Mode.ERASING),
                onCheckedChange = { checked ->
                    val newConfig = if (checked) {
                        drawingConfig.copy(mode = Mode.ERASING)
                    } else {
                        drawingConfig.copy(mode = Mode.DRAWING)
                    }
                    onUpdateDrawingConfig(newConfig)
                }) {
                if (drawingConfig.mode == Mode.ERASING) {
                    Icon(Icons.Default.Send, "Eraser")
                } else {
                    Icon(Icons.Default.Edit, "Pen")
                }

            }
            OutlinedIconButton(onClick = {
                onUpdateDrawingData(DrawingData(drawingData.paths.dropLast(1)))
            }, Modifier.padding(0.dp)) {
                Icon(Icons.Outlined.ArrowBack, "Undo")
            }
            OutlinedIconButton(onClick = { onUpdateDrawingData(DrawingData(listOf())) }) {
                Icon(Icons.Outlined.Delete, "Clear")
            }
            OutlinedIconButton(onClick = {
                onSave(drawingData)
                inViewMode = true
            }) {
                Icon(Icons.Outlined.Done, "Done")
            }
        }
    }
}