package com.example.ideawhirl.ui.components.drawing_board

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@Composable
fun ColorBox(
    expanded: Boolean,
    onExpanded: () -> Unit,
    onCollapsed: () -> Unit,
    currentStrokeColorIndex: Int,
    onStrokeColorChanged: (Int) -> Unit,
    availableStrokeColors: List<Color>,
) {
    Box() {
        if (expanded) {
            Popup(
                onDismissRequest = { onCollapsed() },
                alignment = Alignment.BottomCenter
            ) {
                Column {
                    val colors = availableStrokeColors.zip(0 until 10).toMutableList()
                    val color = colors.removeAt(currentStrokeColorIndex)
                    colors.add(color)
                    (0 until 10).forEach {
                        DrawingToolButton(
                            onClick = {
                                onStrokeColorChanged(colors[it].second)
                                onCollapsed()
                            },
                            backgroundColor = MaterialTheme.colorScheme.secondary.copy(
                                alpha = 0.8f
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(colors[it].first, CircleShape)
                            )
                        }
                    }
                }
            }
        }
        DrawingToolButton(
            onClick = {
                onExpanded()
            }
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(availableStrokeColors[currentStrokeColorIndex], CircleShape)
            )
        }
    }
}

@Composable
fun StrokeWidthBox(
    strokeColor: Color,
    expanded: Boolean,
    onExpanded: () -> Unit,
    onCollapsed: () -> Unit,
    currentStrokeWidth: StrokeWidth,
    onStrokeWidthChanged: (StrokeWidth) -> Unit
) {
    val strokeList = listOf(
        StrokeWidth.Lighter,
        StrokeWidth.Light,
        StrokeWidth.Normal,
        StrokeWidth.Bold,
        StrokeWidth.Bolder
    )
    Box {
        if (expanded) {
            Popup(
                onDismissRequest = { onCollapsed() },
                alignment = Alignment.BottomCenter
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val strokes = (strokeList - currentStrokeWidth).toMutableList()
                    strokes.add(currentStrokeWidth)
                    strokes.forEach {
                        DrawingToolButton(
                            onClick = {
                                onStrokeWidthChanged(it)
                                onCollapsed()
                            },
                            backgroundColor = MaterialTheme.colorScheme.secondary.copy(
                                alpha = 0.8f
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .drawBehind {
                                        val canvasWidth = size.width
                                        val canvasHeight = size.height
                                        val padding = canvasWidth * 0.1
                                        drawLine(
                                            cap = StrokeCap.Round,
                                            strokeWidth = it.toFloat().dp.toPx(),
                                            start = Offset(
                                                x = canvasWidth - padding.dp.toPx(),
                                                y = canvasHeight / 2
                                            ),
                                            end = Offset(x = padding.dp.toPx(), y = canvasHeight / 2),
                                            color = strokeColor
                                        )
                                    },
                            )
                        }
                    }
                }
            }
        }
        DrawingToolButton(onClick = onExpanded) {
            Box(modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val padding = canvasWidth * 0.1
                    drawLine(
                        cap = StrokeCap.Round,
                        strokeWidth = currentStrokeWidth.toFloat().dp.toPx(),
                        start = Offset(x = canvasWidth - padding.dp.toPx(), y = canvasHeight / 2),
                        end = Offset(x = padding.dp.toPx(), y = canvasHeight / 2),
                        color = strokeColor
                    )
                },
            )
        }
    }
}
@Composable
fun EraserWidthBox(
    expanded: Boolean,
    onExpanded: () -> Unit,
    onCollapsed: () -> Unit,
    currentEraserWidth: EraserWidth,
    onEraserWidthChanged: (EraserWidth) -> Unit
) {
    val strokeList = listOf(
        EraserWidth.Lighter,
        EraserWidth.Light,
        EraserWidth.Normal,
        EraserWidth.Bold,
        EraserWidth.Bolder
    )
    Box {
        val eraserColor = MaterialTheme.colorScheme.background
        if (expanded) {
            Popup(
                onDismissRequest = { onCollapsed() },
                alignment = Alignment.BottomCenter
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val strokes = (strokeList - currentEraserWidth).toMutableList()
                    strokes.add(currentEraserWidth)
                    strokes.forEach {
                        DrawingToolButton(
                            onClick = {
                                onEraserWidthChanged(it)
                                onCollapsed()
                            },
                            backgroundColor = MaterialTheme.colorScheme.secondary.copy(
                                alpha = 0.8f
                            )
                        ) {
                            Box(modifier = Modifier
                                .fillMaxSize()
                                .drawBehind {
                                    val canvasWidth = size.width
                                    val canvasHeight = size.height
                                    drawCircle(
                                        color = eraserColor,
                                        center = Offset(canvasWidth / 2, canvasHeight / 2),
                                        radius = it.toFloat().dp.toPx() / 2,
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
        DrawingToolButton(
            onClick = {
                onExpanded()
            }
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    drawCircle(
                        color = eraserColor,
                        center = Offset(canvasWidth / 2, canvasHeight / 2),
                        radius = currentEraserWidth.toFloat().dp.toPx() / 2,
                    )
                },
            )
        }
    }
}
