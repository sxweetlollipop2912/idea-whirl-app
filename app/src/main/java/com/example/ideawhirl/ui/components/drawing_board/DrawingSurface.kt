package com.example.ideawhirl.ui.components.drawing_board

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitDragOrCancellation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.input.pointer.PointerId
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
    var currentPointerId: PointerId? by remember { mutableStateOf(null) }
    val canvasModifier = Modifier
        .fillMaxSize()
        .background(backgroundColor)
        .pointerInput(currentPosition, motionEvent, paths) {
            awaitEachGesture {
                val newPaths = paths.toMutableList()
                when (motionEvent) {
                    MotionEvent.Idle -> {
                        if (currentPointerId != null) {
                            currentPointerId == null
                        }
                        Log.d("DrawingSurface", "Start")
                        val change = awaitFirstDown()
                        currentPosition = Offset(change.position.x, change.position.y)
                        currentPointerId = change.id
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
                        newPaths
                            .last()
                            .start(currentPosition.x, currentPosition.y)
                        previousPosition = currentPosition
                        motionEvent = MotionEvent.Down
                    }

                    MotionEvent.Down -> {
                        motionEvent = MotionEvent.Move
                    }

                    MotionEvent.Move -> {
                        val ptr = awaitDragOrCancellation(currentPointerId!!)
                        if (ptr == null || ptr.id != currentPointerId) {
                            motionEvent = MotionEvent.Up
                            return@awaitEachGesture
                        }
                        Log.d("DrawingSurface", "Draw")
                        val newPos = ptr.position
                        currentPosition = Offset(newPos.x, newPos.y)
                        val dx = abs(currentPosition.x - previousPosition.x)
                        val dy = abs(currentPosition.y - previousPosition.y)
                        newPaths
                            .last()
                            .drawTo(
                                currentPosition.x,
                                currentPosition.y,
                            )
                        previousPosition = currentPosition
                    }

                    MotionEvent.Up -> {
                        newPaths
                            .last()
                            .finish(currentPosition.x, currentPosition.y)
                        currentPosition = Offset.Unspecified
                        previousPosition = Offset.Unspecified
                        motionEvent = MotionEvent.Idle
                        Log.d("DrawingSurface", "Done")
                    }
                }
                onPathsChanged(newPaths)
            }
        }
    Canvas(modifier = canvasModifier) {
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
