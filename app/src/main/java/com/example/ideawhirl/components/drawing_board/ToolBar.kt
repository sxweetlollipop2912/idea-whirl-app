package com.example.ideawhirl.components.drawing_board

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
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
    currentStrokeColor: Color,
    onStrokeColorChanged: (Color) -> Unit
) {
    val colorList = listOf(Color.Black, Color.Green, Color.Blue, Color.Yellow)
    val colorChooserModifier = { color: Color ->
        Modifier
            .size(40.dp)
            .clip(CircleShape)
            .border(2.dp, color = Color.DarkGray, shape = CircleShape)
            .background(color = color)
            .clickable {
                onStrokeColorChanged(color)
                onCollapsed()
            }
    }
    Box {
        if (expanded) {
            Popup(onDismissRequest = { onCollapsed }) {
                Column {
                    val colors = (colorList - currentStrokeColor).toMutableList()
                    colors.add(0, currentStrokeColor)
                    colors.forEach {
                        Box(modifier = colorChooserModifier(it))
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color = currentStrokeColor)
                .border(2.dp, color = Color.DarkGray, shape = CircleShape)
                .clickable {
                    onExpanded()
                }
        )
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
                        Box(modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color = Color.White)
                            .border(2.dp, color = Color.DarkGray, shape = CircleShape)
                            .drawWithContent {
                                val canvasWidth = size.width
                                val canvasHeight = size.height
                                drawLine(
                                    cap = StrokeCap.Round,
                                    strokeWidth = it.toFloat().dp.toPx(),
                                    start = Offset(
                                        x = canvasWidth - 10f.dp.toPx(),
                                        y = canvasHeight / 2
                                    ),
                                    end = Offset(x = 10f.dp.toPx(), y = canvasHeight / 2),
                                    color = strokeColor
                                )
                            }
                            .clickable {
                                onStrokeWidthChanged(it)
                                onCollapsed()
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color = Color.White)
                .border(2.dp, color = Color.DarkGray, shape = CircleShape)
                .drawWithContent {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    drawLine(
                        cap = StrokeCap.Round,
                        strokeWidth = currentStrokeWidth.toFloat().dp.toPx(),
                        start = Offset(x = canvasWidth - 10f.dp.toPx(), y = canvasHeight / 2),
                        end = Offset(x = 10f.dp.toPx(), y = canvasHeight / 2),
                        color = strokeColor
                    )
                }
                .clickable {
                    onExpanded()
                }
        )
    }
}

@Composable
fun ToolBar() {
}
