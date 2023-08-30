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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import java.io.Serializable
import kotlin.math.abs

enum class MotionEvent {
    Idle, Down, Move, Up
}

class DrawingPath(val strokeWidth: StrokeWidth, val strokeColor: Color) : Serializable {
    private val points: MutableList<Pair<Float, Float>> = mutableListOf()
    private val path = Path()
    fun drawTo(x: Float, y: Float) {
        val (fl1, fl2) = points.last()
        points.add(Pair(x, y))
        val x1 = (x + fl1) / 2
        val x2 = (y + fl2) / 2
        path.quadraticBezierTo(fl1, fl2, x1, x2)
    }

    fun start(x: Float, y: Float) {
        points.clear()
        points.add(Pair(x, y))
        path.moveTo(x, y)
    }

    fun finish(x: Float, y: Float) {
        points.add(Pair(x, y))
        path.lineTo(x, y)
    }

    val drawData get() = path
}

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
    val paths: MutableState<List<DrawingPath>> = remember { mutableStateOf(listOf()) }
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
    DrawingBoard(paths, strokeColor, strokeWidth)
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
        Button(onClick = { paths.value = paths.value.dropLast(1) }) {
            Text(text = "Undo")
        }
    }
}

const val OFFSET_TOLERANCE = 4

@Composable
fun DrawingBoard(
    paths: MutableState<List<DrawingPath>>,
    strokeColor: Color,
    strokeWidth: StrokeWidth
) {
    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    // This is our motion event we get from touch motion
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    // This is previous motion event before next touch is saved into this current position
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }
    val canvasModifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()
                    // handle pointer event
                    when (event.type) {
                        PointerEventType.Press -> {
                            motionEvent = MotionEvent.Down
                            val change = event.changes.last().position
                            currentPosition = Offset(change.x, change.y)
                            Log.d("DrawingBoard", "press at: $currentPosition")
                        }

                        PointerEventType.Move -> {
                            val change = event.changes.last().position
                            currentPosition = Offset(change.x, change.y)
                        }

                        PointerEventType.Release -> {
                            motionEvent = MotionEvent.Up
                            val change = event.changes.last().position
                            currentPosition = Offset(change.x, change.y)
                            Log.d("DrawingBoard", "release at: $currentPosition")
                        }
                    }
                }
            }
        }
    Canvas(modifier = canvasModifier) {
        when (motionEvent) {
            MotionEvent.Down -> {
                paths.value += DrawingPath(
                    strokeColor = strokeColor,
                    strokeWidth = strokeWidth
                )
                Log.d("DrawingBoard", "Add new path")
                paths.value.last().start(currentPosition.x, currentPosition.y)
                previousPosition = currentPosition
                motionEvent = MotionEvent.Move
            }

            MotionEvent.Move -> {
                val dx = abs(currentPosition.x - previousPosition.x)
                val dy = abs(currentPosition.y - previousPosition.y)
                if (dx >= OFFSET_TOLERANCE || dy >= OFFSET_TOLERANCE) {
                    paths.value.last().drawTo(
                        currentPosition.x,
                        currentPosition.y,
                    )
                }
                previousPosition = currentPosition
            }

            MotionEvent.Up -> {
                paths.value.last().finish(currentPosition.x, currentPosition.y)
                currentPosition = Offset.Unspecified
                previousPosition = currentPosition
                motionEvent = MotionEvent.Idle
            }

            else -> Unit
        }

        for (path in paths.value) {
            drawPath(
                color = path.strokeColor,
                path = path.drawData,
                style = Stroke(
                    width = path.strokeWidth.toFloat().dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}