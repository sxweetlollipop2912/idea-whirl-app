package com.example.ideawhirl.components.drawing_board

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Transient

enum class MotionEvent {
    Idle, Down, Move, Up
}

@kotlinx.serialization.Serializable
class DrawingPath(
    val strokeWidth: StrokeWidth,
    val strokeColor: Int,
) {
    val pointXs: MutableList<Float> = mutableListOf()
    val pointYs: MutableList<Float> = mutableListOf()

    constructor(
        strokeWidth: StrokeWidth,
        strokeColor: Int,
        pointXs: List<Float>,
        pointYs: List<Float>,
    ) : this(strokeWidth, strokeColor) {
        this.pointXs.clear()
        this.pointYs.clear()
        this.pointXs.addAll(pointXs)
        this.pointYs.addAll(pointYs)
    }

    @Transient
    private lateinit var path: Path

    init {
        path = Path()
        if (pointXs.size != pointYs.size) {
            throw (Throwable("Invalid state"))
        }
        if (pointXs.isNotEmpty()) {
            path.moveTo(pointXs.first(), pointYs.first())
            var lastPoints = Pair(pointXs.first(), pointYs.first())
            pointXs.zip(pointYs).drop(1).dropLast(1).forEach {
                val (fl1, fl2) = lastPoints
                val (x, y) = it
                val x1 = (x + fl1) / 2
                val x2 = (y + fl2) / 2
                path.quadraticBezierTo(fl1, fl2, x1, x2)
                lastPoints = Pair(x, y)
            }
            path.lineTo(pointXs.last(), pointYs.last())
        }
    }

    fun initFromPoints() {
        path = Path()
        if (pointXs.size != pointYs.size) {
            throw (Throwable("Invalid state"))
        }
        if (pointXs.isNotEmpty()) {
            path.moveTo(pointXs.first(), pointYs.first())
            var lastPoints = Pair(pointXs.first(), pointYs.first())
            pointXs.zip(pointYs).drop(1).dropLast(1).forEach {
                val (fl1, fl2) = lastPoints
                val (x, y) = it
                val x1 = (x + fl1) / 2
                val x2 = (y + fl2) / 2
                path.quadraticBezierTo(fl1, fl2, x1, x2)
                lastPoints = Pair(x, y)
            }
            path.lineTo(pointXs.last(), pointYs.last())
        }
    }

    fun drawTo(x: Float, y: Float) {
        val (fl1, fl2) = Pair(pointXs.last(), pointYs.last())
        pointXs.add(x)
        pointYs.add(y)
        val x1 = (x + fl1) / 2
        val x2 = (y + fl2) / 2
        path.quadraticBezierTo(fl1, fl2, x1, x2)
    }

    fun start(x: Float, y: Float) {
        path = Path()
        pointXs.clear()
        pointYs.clear()
        pointXs.add(x)
        pointYs.add(y)
        path.moveTo(x, y)
    }

    fun finish(x: Float, y: Float) {
        pointXs.add(x)
        pointYs.add(y)
        path.lineTo(x, y)
    }

    val drawData get() = path

    val pointsNum get() = pointXs.size
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
    var paths: List<DrawingPath> by remember { mutableStateOf(listOf()) }
    var strokeWidth by remember { mutableStateOf(StrokeWidth.Normal) }
    var strokeColor by remember { mutableStateOf(Color.Black) }
    var showColorChooser by remember { mutableStateOf(false) }
    var showStrokeWidthChooser by remember { mutableStateOf(false) }
    var inViewMode by remember { mutableStateOf(false) }

    if (!inViewMode) {
        DrawingSurface(paths, { newPaths -> paths = newPaths }, strokeColor, strokeWidth)
    } else {
        DisplayBoard(paths) {
            inViewMode = false
        }
    }
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
        ColorBox(
            expanded = showColorChooser,
            onExpanded = { showColorChooser = true },
            onCollapsed = { showColorChooser = false },
            currentStrokeColor = strokeColor,
            onStrokeColorChanged = { color -> strokeColor = color }
        )
        Spacer(modifier = Modifier.width(5.dp))
        StrokeWidthBox(
            strokeColor = strokeColor,
            expanded = showStrokeWidthChooser,
            onExpanded = { showStrokeWidthChooser = true },
            onCollapsed = { showStrokeWidthChooser = false },
            currentStrokeWidth = strokeWidth,
            onStrokeWidthChanged = { width -> strokeWidth = width }
        )
        Button(onClick = { paths = paths.dropLast(1) }) {
            Text(text = "Undo")
        }
        FilledIconToggleButton(
            checked = !inViewMode,
            onCheckedChange = { checked -> inViewMode = !checked }) {
        }
    }
}

const val OFFSET_TOLERANCE = 2