package com.example.ideawhirl.ui.components.drawing_board

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import androidx.compose.ui.graphics.drawscope.Stroke as CanvasStroke

const val TOLERANT_THRESHOLD = 20

// Trim the drawing data because preview is small, so the data
// doesn't have to contain as many control points
internal fun trimDrawingData(drawingData: DrawingData): DrawingData {
    if (drawingData.isEmpty()) {
        return drawingData
    }
    val newStrokeList = mutableListOf<Stroke>()
    for (stroke in drawingData.paths) {
        val newPathListX = mutableListOf<Float>()
        val newPathListY = mutableListOf<Float>()
        for (i in 0 until stroke.pointsNum) {
            if (i == 0 || i == stroke.pointsNum - 1) {
                newPathListX.add(stroke.pointXs[i])
                newPathListY.add(stroke.pointYs[i])
            } else if (abs(stroke.pointXs[i] - newPathListX.last()) > TOLERANT_THRESHOLD
                || abs(stroke.pointYs[i] - newPathListY.last()) > TOLERANT_THRESHOLD
            ) {
                newPathListX.add(stroke.pointXs[i])
                newPathListY.add(stroke.pointYs[i])
            }
        }
        stroke.pointXs.clear()
        stroke.pointYs.clear()
        stroke.pointXs.addAll(newPathListX)
        stroke.pointYs.addAll(newPathListY)
        newStrokeList += stroke
    }
    return drawingData.copy(paths = newStrokeList.toList())
}

@Composable
fun PreviewDisplayBoard(
    drawingData: DrawingData,
    availableStrokeColors: List<Color>,
    backgroundColor: Color,
) {
    if (drawingData.paths.isEmpty()) {
        return Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {}
    }

    var trimmedDrawingData by remember(drawingData) {
        mutableStateOf((DrawingData.emptyData()))
    }
    var playAnimation by remember { mutableStateOf(true) }
    var currentSegment by remember { mutableIntStateOf(0) }
    var currentDrawingPath by remember { mutableIntStateOf(0) }
    var partialPathLists: List<Path> by remember() { mutableStateOf(listOf(Path())) }
    LaunchedEffect(key1 = drawingData) {
        trimmedDrawingData = trimDrawingData(drawingData)
    }
    LaunchedEffect(key1 = playAnimation, key2 = currentSegment) {
        if (playAnimation) {
            val currentPath = trimmedDrawingData.paths[currentDrawingPath]
            when (currentSegment) {
                0 -> {
                    partialPathLists.last()
                        .moveTo(currentPath.pointXs.first(), currentPath.pointYs.first())
                    currentSegment += 1
                }

                in 1 until currentPath.pointsNum - 1 -> {
                    val (fl1, fl2) = Pair(
                        currentPath.pointXs[currentSegment - 1],
                        currentPath.pointYs[currentSegment - 1]
                    )
                    val x1 = (currentPath.pointXs[currentSegment] + fl1) / 2
                    val x2 = (currentPath.pointYs[currentSegment] + fl2) / 2
                    partialPathLists.last().quadraticBezierTo(fl1, fl2, x1, x2)
                    currentSegment += 1
                }

                currentPath.pointsNum - 1 -> {
                    partialPathLists.last()
                        .lineTo(currentPath.pointXs.last(), currentPath.pointYs.last())
                    currentSegment = 0
                    currentDrawingPath += 1
                    partialPathLists += Path()
                }
            }
            if (currentDrawingPath >= trimmedDrawingData.paths.size) {
                playAnimation = false
                currentDrawingPath--
            }
        }
    }
    BoxWithConstraints {
        if (!drawingData.availableForPreview()) {
            throw AssertionError(
                """
                Drawing data does not contain canvas size, therefore, cannot be previewed.
            """.trimIndent()
            )
        }
        val vScale = maxWidth / drawingData.canvasSizeX!!.dp
        val hScale = maxHeight / drawingData.canvasSizeY!!.dp
        val scale = if (hScale > vScale) {
            vScale
        } else {
            hScale
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .scale(scale)
        ) {
            for (i in 0..currentDrawingPath) {
                val pathColorIndex = drawingData.paths[i].strokeColorIndex() ?: -1
                val pathColor = if (pathColorIndex == -1) {
                    backgroundColor
                } else {
                    availableStrokeColors[pathColorIndex]
                }
                drawPath(
                    color = pathColor,
                    path = partialPathLists[i],
                    style = CanvasStroke(
                        width = drawingData.paths[i].strokeWidth().dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }

    }
}
