package com.example.ideawhirl.components.drawing_board

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.ideawhirl.components.drawing_board.DrawingPath
import com.example.ideawhirl.components.drawing_board.MotionEvent
import com.example.ideawhirl.components.drawing_board.OFFSET_TOLERANCE
import com.example.ideawhirl.components.drawing_board.StrokeWidth
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.math.abs

@Composable
fun DrawingSurface(
    paths: List<DrawingPath>,
    onPathsChanged: (List<DrawingPath>) -> Unit,
    strokeColor: Color,
    strokeWidth: StrokeWidth
) {
    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
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
                        }

                        PointerEventType.Move -> {
                            val change = event.changes.last().position
                            currentPosition = Offset(change.x, change.y)
                        }

                        PointerEventType.Release -> {
                            motionEvent = MotionEvent.Up
                            val change = event.changes.last().position
                            currentPosition = Offset(change.x, change.y)
                        }
                    }
                }
            }
        }
    Canvas(modifier = canvasModifier) {
        var newPaths = paths
        when (motionEvent) {
            MotionEvent.Down -> {
                newPaths += DrawingPath(
                    strokeColor = strokeColor.toArgb(),
                    strokeWidth = strokeWidth
                )
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
        onPathsChanged(newPaths)

        for (path in paths) {
            drawPath(
                color = Color(path.strokeColor),
                path = Json.decodeFromString<DrawingPath>(Json.encodeToString(path)).drawData,
                style = Stroke(
                    width = path.strokeWidth.toFloat().dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}
