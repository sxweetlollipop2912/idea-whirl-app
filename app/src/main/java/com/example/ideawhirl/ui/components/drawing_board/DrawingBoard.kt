package com.example.ideawhirl.ui.components.drawing_board

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Undo
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ideawhirl.R

enum class MotionEvent {
    Idle, Down, Move, Up
}

const val OFFSET_TOLERANCE = 2

@Composable
fun DrawingBoard(
    availableStrokeColors: List<Color>,
    backgroundColor: Color,
    drawingData: DrawingData,
    onUpdateDrawingData: (DrawingData) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    drawingConfig: DrawingConfig,
    onUpdateDrawingConfig: (DrawingConfig) -> Unit,
) {
    if (availableStrokeColors.size != 10) {
        throw (Throwable("Invalid arguments, colors list must have exactly 10 colors."))
    }
    var showEraserWidthChooser by rememberSaveable { mutableStateOf(false) }
    var showColorChooser by rememberSaveable { mutableStateOf(false) }
    var showStrokeWidthChooser by rememberSaveable { mutableStateOf(false) }
    var inViewMode by rememberSaveable { mutableStateOf(false) }

    var isToolBarExpanded by rememberSaveable { mutableStateOf(!inViewMode) }

    Box(modifier = Modifier
        .pointerInput(Unit) {
            detectTapGestures {
                showStrokeWidthChooser = false
                showColorChooser = false
            }
        }) {
        if (!inViewMode) {
            DrawingSurface(
                drawingData,
                { newData -> onUpdateDrawingData(newData) },
                availableStrokeColors,
                backgroundColor,
                drawingConfig = drawingConfig,
            )
        } else {
            ReplayableDisplayBoard(
                drawingData,
                { inViewMode = false },
                availableStrokeColors,
                backgroundColor
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(80.dp)
                .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp),
        ) {
            IconButton(
                onClick = onBack,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground,
                ),
                modifier = Modifier
                    .align(Alignment.TopStart),
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBackIosNew,
                    contentDescription = "Back"
                )
            }
            if (!inViewMode) {
                FloatingActionButton(
                    modifier = Modifier
                        .size(42.dp)
                        .align(Alignment.TopEnd),
                    onClick = {
                        onSave()
                        inViewMode = true
                    },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Done,
                        contentDescription = "Done",
                    )
                }
            } else {
                FloatingActionButton(
                    modifier = Modifier
                        .size(42.dp)
                        .align(Alignment.TopEnd),
                    onClick = {
                        inViewMode = false
                        isToolBarExpanded = true
                    },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Brush,
                        contentDescription = "Edit",
                    )
                }
            }
        }
        if (!inViewMode) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(top = 0.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
            ) {
                Row(
                    modifier = Modifier
                        .align(
                            if (isToolBarExpanded)
                                Alignment.TopCenter
                            else
                                Alignment.TopEnd
                        )
                        .shadow(
                            elevation = 2.dp,
                            shape = MaterialTheme.shapes.small,
                        )
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(5.dp)
                        .animateContentSize(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (!inViewMode && isToolBarExpanded) {
                        DrawingToolButton(
                            onClick = {
                                onUpdateDrawingData(DrawingData(listOf(), null, null))
                            }
                        ) {
                            Icon(
                                Icons.Outlined.DeleteForever,
                                "Clear",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                        DrawingToolButton(
                            onClick = {
                                onUpdateDrawingData(
                                    drawingData.copy(
                                        paths = drawingData.paths.dropLast(
                                            1
                                        )
                                    )
                                )
                            }
                        ) {
                            Icon(Icons.Outlined.Undo, "Undo")
                        }
                        if (drawingConfig.mode == Mode.ERASING) {
                            DrawingToolButton(
                                onClick = {
                                    val newConfig = drawingConfig.copy(mode = Mode.DRAWING)
                                    onUpdateDrawingConfig(newConfig)
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_ink_eraser),
                                    "Eraser"
                                )
                            }
                        } else if (drawingConfig.mode == Mode.DRAWING) {
                            DrawingToolButton(
                                onClick = {
                                    val newConfig = drawingConfig.copy(mode = Mode.ERASING)
                                    onUpdateDrawingConfig(newConfig)
                                }
                            ) {
                                Icon(Icons.Default.Edit, "Pen")
                            }
                        }
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
                        DrawingToolButton(
                            onClick = {
                                isToolBarExpanded = false
                            }
                        ) {
                            Icon(Icons.Outlined.Close, "Close tool bar")
                        }
                    } else if (!inViewMode) {
                        DrawingToolButton(
                            onClick = {
                                isToolBarExpanded = true
                            }
                        ) {
                            Icon(Icons.Outlined.Brush, "Open tool bar")
                        }
                    } else {
                        DrawingToolButton(
                            onClick = {
                                inViewMode = false
                                isToolBarExpanded = true
                            }
                        ) {
                            Icon(Icons.Outlined.Brush, "Switch to edit mode")
                        }
                    }
                }
            }
        }
    }
}