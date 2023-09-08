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
import androidx.compose.ui.graphics.drawscope.Stroke as CanvasStroke
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun DrawingSurface(
    paths: List<Stroke>,
    onPathsChanged: (List<Stroke>) -> Unit,
    availableStrokeColors: List<Color>,
    backgroundColor: Color,
    drawingConfig: DrawingConfig,
) {
    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }
    var currentPointerId: PointerId? by remember { mutableStateOf(null) }
    var currentPath: Stroke? by remember(key1 = paths) {
        mutableStateOf(
            null
        )
    }
    val canvasModifier = Modifier
        .fillMaxSize()
        .background(backgroundColor)
        .pointerInput(currentPosition, motionEvent, paths, drawingConfig) {
            awaitEachGesture {
                when (motionEvent) {
                    MotionEvent.Idle -> {
                        currentPosition = Offset.Unspecified
                        previousPosition = Offset.Unspecified
                        if (currentPointerId != null) {
                            currentPointerId == null
                        }
                        val change = awaitFirstDown()
                        currentPosition = Offset(change.position.x, change.position.y)
                        currentPointerId = change.id
                        currentPath = when (drawingConfig.mode) {
                            Mode.DRAWING -> {
                                DrawingPath(
                                    strokeColorIndex = drawingConfig.strokeColorIndex,
                                    strokeWidth = drawingConfig.strokeWidth
                                )
                            }

                            Mode.ERASING -> {
                                EraserPath(drawingConfig.eraserWidth)
                            }
                        }
                        currentPath?.start(currentPosition.x, currentPosition.y)
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
                        val newPos = ptr.position
                        currentPosition = Offset(newPos.x, newPos.y)
                        val dx = abs(currentPosition.x - previousPosition.x)
                        val dy = abs(currentPosition.y - previousPosition.y)
                        if (dx >= OFFSET_TOLERANCE || dy >= OFFSET_TOLERANCE) {
                            currentPath = currentPath?.apply {
                                drawTo(
                                    currentPosition.x,
                                    currentPosition.y,
                                )
                            }
                        }
                        previousPosition = currentPosition
                    }

                    MotionEvent.Up -> {
                        currentPath = currentPath?.apply {
                            drawTo(currentPosition.x, currentPosition.y)
                        }
                        currentPath = currentPath?.apply {
                            finish(currentPosition.x, currentPosition.y)
                        }
                        currentPosition = Offset.Unspecified
                        previousPosition = Offset.Unspecified
                        motionEvent = MotionEvent.Idle
                        var newPaths = paths
                        newPaths += (currentPath!!)
                        onPathsChanged(newPaths)
                    }
                }
            }
        }
    Canvas(modifier = canvasModifier) {
        val pathColor = { path: Stroke ->
            val pathColorIndex = path!!.strokeColorIndex() ?: -1
            if (pathColorIndex == -1) {
                backgroundColor
            } else {
                availableStrokeColors[pathColorIndex]
            }
        }
        val draw = { path: Stroke ->
            drawPath(
                color = pathColor(path),
                path = path.drawData,
                style = CanvasStroke(
                    width = path.strokeWidth().dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
        for (path in paths) {
            draw(path)
        }
        if (currentPath == null) {
            return@Canvas
        }
        draw(currentPath!!)
    }
}
