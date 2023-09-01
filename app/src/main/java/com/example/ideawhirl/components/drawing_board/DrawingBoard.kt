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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Transient

enum class MotionEvent {
    Idle, Down, Move, Up
}

const val OFFSET_TOLERANCE = 2

@Composable
fun DrawingBoard(availableStrokeColors: List<Color>, backgroundColor: Color) {
    if (availableStrokeColors.size != 10) {
        throw (Throwable("Invalid arguments, colors list must have exactly 10 colors."))
    }
    var usingEraser by remember {
        mutableStateOf(false)
    }
    var eraserWidth by remember { mutableStateOf(EraserWidth.Normal) }
    var showEraserWidthChooser by remember { mutableStateOf(false) }
    var paths: List<Stroke> by remember { mutableStateOf(listOf()) }
    var strokeWidth by remember { mutableStateOf(StrokeWidth.Normal) }
    var strokeColorIndex by remember { mutableStateOf(0) }
    var showColorChooser by remember { mutableStateOf(false) }
    var showStrokeWidthChooser by remember { mutableStateOf(false) }
    var inViewMode by remember { mutableStateOf(false) }

    if (!inViewMode) {
        DrawingSurface(
            paths,
            { newPaths -> paths = newPaths },
            strokeColorIndex,
            strokeWidth,
            availableStrokeColors,
            backgroundColor,
            erasing = usingEraser,
            eraserWidth = eraserWidth,
        )
    } else {
        DisplayBoard(paths, { inViewMode = false }, availableStrokeColors, backgroundColor)
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
                currentStrokeColorIndex = strokeColorIndex,
                onStrokeColorChanged = { index -> strokeColorIndex = index },
                availableStrokeColors,
            )
            if (usingEraser) {
                EraserWidthBox(
                    expanded = showEraserWidthChooser,
                    onExpanded = { showEraserWidthChooser = true },
                    onCollapsed = { showEraserWidthChooser = false },
                    currentEraserWidth = eraserWidth,
                    onEraserWidthChanged = { newWidth -> eraserWidth = newWidth },
                )
            } else {
                StrokeWidthBox(
                    strokeColor = availableStrokeColors[strokeColorIndex],
                    expanded = showStrokeWidthChooser,
                    onExpanded = { showStrokeWidthChooser = true },
                    onCollapsed = { showStrokeWidthChooser = false },
                    currentStrokeWidth = strokeWidth,
                    onStrokeWidthChanged = { width -> strokeWidth = width }
                )
            }
            IconToggleButton(
                checked = usingEraser,
                onCheckedChange = { checked -> usingEraser = checked }) {
                if (usingEraser) {
                    Icon(Icons.Default.Send, "Eraser")
                } else {
                    Icon(Icons.Default.Edit, "Pen")
                }

            }
            OutlinedIconButton(onClick = { paths = paths.dropLast(1) }, Modifier.padding(0.dp)) {
                Icon(Icons.Outlined.ArrowBack, "Undo")
            }
            OutlinedIconButton(onClick = { paths = listOf() }) {
                Icon(Icons.Outlined.Delete, "Clear")
            }
            OutlinedIconButton(onClick = { inViewMode = true }) {
                Icon(Icons.Outlined.Done, "Done")
            }
        }
    }
}