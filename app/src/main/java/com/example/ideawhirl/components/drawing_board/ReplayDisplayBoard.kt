package com.example.ideawhirl.components.drawing_board

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun DisplayBoard(
    paths: List<DrawingPath>,
    onEditRequested: () -> Unit,
    availableStrokeColors: List<Color>,
    backgroundColor: Color,
) {
    var playAnimation by remember { mutableStateOf(true) }
    var currentSegment by remember { mutableStateOf(0) }
    var currentDrawingPath by remember { mutableStateOf(0) }
    var partialPathLists: List<Path> by remember() { mutableStateOf(listOf(Path())) }
    LaunchedEffect(key1 = playAnimation, key2 = currentSegment) {
        if (playAnimation) {
            delay(5)
            val currentPath = paths[currentDrawingPath]
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
            if (currentDrawingPath >= paths.size) {
                playAnimation = false
                currentDrawingPath--
            }
        }
    }
    Canvas(modifier = Modifier
        .fillMaxSize()
        .background(backgroundColor)
        .clickable { onEditRequested() }) {
        for (i in 0 .. currentDrawingPath) {
            drawPath(
                color = Color(availableStrokeColors[paths[i].strokeColorIndex].toArgb()),
                path = partialPathLists[i],
                style = Stroke(
                    width = paths[i].strokeWidth.toFloat().dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }

}
