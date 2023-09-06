package com.example.ideawhirl.ui.components.drawing_board

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitDragOrCancellation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun DrawingSurface(
    paths: List<com.example.ideawhirl.ui.components.drawing_board.Stroke>,
    onPathsChanged: (List<com.example.ideawhirl.ui.components.drawing_board.Stroke>) -> Unit,
    availableStrokeColors: List<Color>,
    backgroundColor: Color,
    drawingConfig: DrawingConfig,
) {
    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }
    val canvasModifier = Modifier
        .fillMaxSize()
        .background(backgroundColor)
        .pointerInput(Unit) {
            awaitEachGesture {
                while (true) {
                    val change = awaitFirstDown()
                    motionEvent = MotionEvent.Down
                    currentPosition = Offset(change.position.x, change.position.y)
                    val currentPointerId = change.id
                    withTimeout(10) {}
                    while (true) {
                        val ptr = awaitDragOrCancellation(currentPointerId)
                        if (ptr == null || ptr.id != currentPointerId) {
                            motionEvent = MotionEvent.Up
                            break
                        }
                        val newPos = ptr.position
                        currentPosition = Offset(newPos.x, newPos.y)
                    }
                    motionEvent = MotionEvent.Up
                }
            }
        }
    Canvas(modifier = canvasModifier) {
        val newPaths = paths.toMutableList()
        when (motionEvent) {
            MotionEvent.Down -> {
                when (drawingConfig.mode) {
                    Mode.DRAWING -> {
                        newPaths.add(
                            DrawingPath(
                                strokeColorIndex = drawingConfig.strokeColorIndex,
                                strokeWidth = drawingConfig.strokeWidth
                            )
                        )
                    }

                    Mode.ERASING -> {
                        newPaths.add(EraserPath(drawingConfig.eraserWidth))
                    }
                }
                newPaths.last().start(currentPosition.x, currentPosition.y)
                previousPosition = currentPosition
                motionEvent = MotionEvent.Move
            }

            MotionEvent.Move -> {
                val dx = abs(currentPosition.x - previousPosition.x)
                val dy = abs(currentPosition.y - previousPosition.y)
                if (dx >= OFFSET_TOLERANCE || dy >= OFFSET_TOLERANCE) {
                    newPaths.last().drawTo(
                        currentPosition.x,
                        currentPosition.y,
                    )
                }
                previousPosition = currentPosition
            }

            MotionEvent.Up -> {
                newPaths.last().finish(currentPosition.x, currentPosition.y)
                currentPosition = Offset.Unspecified
                previousPosition = currentPosition
                motionEvent = MotionEvent.Idle
            }

            else -> Unit
        }
        onPathsChanged(newPaths.toList())

        for (path in paths) {
            val pathColorIndex = path.strokeColorIndex() ?: -1
            val pathColor = if (pathColorIndex == -1) {
                backgroundColor
            } else {
                availableStrokeColors[pathColorIndex]
            }
            drawPath(
                color = pathColor,
                path = path.drawData,
                style = Stroke(
                    width = path.strokeWidth().dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}
