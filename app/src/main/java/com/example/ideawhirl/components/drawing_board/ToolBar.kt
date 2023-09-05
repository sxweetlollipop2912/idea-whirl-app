package com.example.ideawhirl.components.drawing_board

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
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
            Popup(onDismissRequest = { onCollapsed() }) {
                Column {
                    val colors = availableStrokeColors.zip(0 until 10).toMutableList()
                    val color = colors.removeAt(currentStrokeColorIndex)
                    colors.add(0, color)
                    (0 until 10).forEach {
                        OutlinedIconButton(
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = colors[it].first
                            ),
                            onClick = {
                                onStrokeColorChanged(colors[it].second)
                                onCollapsed()
                            }) {}
                    }
                }
            }
        }
        OutlinedIconButton(
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = availableStrokeColors[currentStrokeColorIndex]
            ),
            onClick = {
                onExpanded()
            }) {}
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
            Popup(onDismissRequest = { onCollapsed() }) {
                Column {
                    val strokes = (strokeList - currentStrokeWidth).toMutableList()
                    strokes.add(0, currentStrokeWidth)
                    strokes.forEach {
                        OutlinedIconButton(
                            modifier = Modifier
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
                            onClick = {
                                onStrokeWidthChanged(it)
                                onCollapsed()
                            }) {}
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
        OutlinedIconButton(
            modifier = Modifier
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
            onClick = {
                onExpanded()
            }
        ) {}
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
        if (expanded) {
            Popup(onDismissRequest = { onCollapsed() }) {
                Column {
                    val strokes = (strokeList - currentEraserWidth).toMutableList()
                    strokes.add(0, currentEraserWidth)
                    strokes.forEach {
                        OutlinedIconButton(
                            modifier = Modifier
                                .drawBehind {
                                    val canvasWidth = size.width
                                    val canvasHeight = size.height
                                    drawCircle(
                                        color = Color.DarkGray,
                                        center = Offset(canvasWidth / 2, canvasHeight / 2),
                                        radius = it.toFloat().dp.toPx() / 2,
                                    )
                                },
                            onClick = {
                                onEraserWidthChanged(it)
                                onCollapsed()
                            }) {}
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
        OutlinedIconButton(
            modifier = Modifier
                .drawBehind {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    drawCircle(
                        color = Color.DarkGray,
                        center = Offset(canvasWidth / 2, canvasHeight / 2),
                        radius = currentEraserWidth.toFloat().dp.toPx() / 2,
                    )
                },
            onClick = {
                onExpanded()
            }
        ) {}
    }
}
