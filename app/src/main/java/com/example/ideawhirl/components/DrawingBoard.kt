package com.example.ideawhirl.components

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import com.smarttoolfactory.gesture.pointerMotionEvents

enum class MotionEvent {
    Idle, Down, Move, Up
}

data class PathData(
    val strokeColor: Color,
    val strokeWidth: StrokeWidth,
    val path: Path = Path(),
)

enum class StrokeWidth {
    Lighter,
    Light,
    Normal,
    Bold,
    Bolder;

    fun toFloat(): Float {
        return when (this) {
            Lighter -> 1f
            Light -> 2f
            Normal -> 3f
            Bold -> 4f
            Bolder -> 5f
        }
    }
}

@Composable
fun DrawingPanel() {
    val colorList = listOf(Color.Black, Color.Green, Color.Blue, Color.Yellow)
    val strokeList = listOf(
        StrokeWidth.Lighter,
        StrokeWidth.Light,
        StrokeWidth.Normal,
        StrokeWidth.Bold,
        StrokeWidth.Bolder
    )
    var strokeWidth by remember {
        mutableStateOf(StrokeWidth.Normal)
    }
    var strokeColor by remember {
        mutableStateOf(Color.Black)
    }
    var showColorChooser by remember {
        mutableStateOf(false)
    }
    var showStrokeWidthChooser by remember {
        mutableStateOf(false)
    }
    val colorChooserModifier = { color: Color ->
        Modifier
            .size(40.dp)
            .clip(CircleShape)
            .border(2.dp, color = Color.DarkGray, shape = CircleShape)
            .background(color = color)
            .clickable {
                strokeColor = color
                showColorChooser = false
            }
    }
    DrawingBoard(strokeColor, strokeWidth)
    if (showColorChooser || showStrokeWidthChooser) {
        Box(modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures {
                    showStrokeWidthChooser = false
                    showColorChooser = false
                }
            })
    }
    Row(modifier = Modifier.offset(5.dp, 5.dp)) {
        Box {
            if (showColorChooser) {
                Popup(onDismissRequest = { showColorChooser = false }) {
                    Column {
                        val colors = (colorList - strokeColor).toMutableList()
                        colors.add(0, strokeColor)
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
                    .background(color = strokeColor)
                    .border(2.dp, color = Color.DarkGray, shape = CircleShape)
                    .clickable {
                        showColorChooser = true
                    }
            )
        }
        Spacer(modifier = Modifier.width(5.dp))
        Box {
            if (showStrokeWidthChooser) {
                Popup(onDismissRequest = { showStrokeWidthChooser = false }) {
                    Column {
                        val strokes = (strokeList - strokeWidth).toMutableList()
                        strokes.add(0, strokeWidth)
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
                                    strokeWidth = it
                                    showStrokeWidthChooser = false
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
                            strokeWidth = strokeWidth.toFloat().dp.toPx(),
                            start = Offset(x = canvasWidth - 10f.dp.toPx(), y = canvasHeight / 2),
                            end = Offset(x = 10f.dp.toPx(), y = canvasHeight / 2),
                            color = strokeColor
                        )
                    }
                    .clickable {
                        showStrokeWidthChooser = true
                    }
            )
        }
    }
}

@Composable
fun DrawingBoard(strokeColor: Color, strokeWidth: StrokeWidth) {
    var paths: List<PathData> by remember { mutableStateOf(listOf()) }
    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    // This is our motion event we get from touch motion
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    // This is previous motion event before next touch is saved into this current position
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }
    val canvasModifier = Modifier
        .fillMaxSize()
        .pointerMotionEvents(
            onDown = { pointerInputChange: PointerInputChange ->
                currentPosition = pointerInputChange.position
                motionEvent = MotionEvent.Down
                pointerInputChange.consume()
            },
            onMove = { pointerInputChange: PointerInputChange ->
                currentPosition = pointerInputChange.position
                motionEvent = MotionEvent.Move
                pointerInputChange.consume()
            },
            onUp = { pointerInputChange: PointerInputChange ->
                motionEvent = MotionEvent.Up
                pointerInputChange.consume()
            },
            delayAfterDownInMillis = 25L
        )
    Canvas(modifier = canvasModifier) {
        when (motionEvent) {
            MotionEvent.Down -> {
                paths = paths + PathData(strokeColor = strokeColor, strokeWidth = strokeWidth)
                paths.last().path.moveTo(currentPosition.x, currentPosition.y)
                previousPosition = currentPosition
            }

            MotionEvent.Move -> {
                paths.last().path.quadraticBezierTo(
                    previousPosition.x,
                    previousPosition.y,
                    (previousPosition.x + currentPosition.x) / 2,
                    (previousPosition.y + currentPosition.y) / 2
                )
                previousPosition = currentPosition
            }

            MotionEvent.Up -> {
                paths.last().path.lineTo(currentPosition.x, currentPosition.y)
                currentPosition = Offset.Unspecified
                previousPosition = currentPosition
                motionEvent = MotionEvent.Idle
            }

            else -> Unit
        }

        for (path in paths) {
            drawPath(
                color = path.strokeColor,
                path = path.path,
                style = Stroke(
                    width = path.strokeWidth.toFloat().dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}