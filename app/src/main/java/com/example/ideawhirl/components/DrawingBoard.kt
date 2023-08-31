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
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SnapshotMutationPolicy
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlinx.coroutines.delay
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.math.abs

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
        if (!pointXs.isEmpty()) {
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
            Popup(onDismissRequest = { onCollapsed }) {
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
fun DrawingPanel() {
    var paths: List<DrawingPath> by remember { mutableStateOf(listOf()) }
    var strokeWidth by remember { mutableStateOf(StrokeWidth.Normal) }
    var strokeColor by remember { mutableStateOf(Color.Black) }
    var showColorChooser by remember { mutableStateOf(false) }
    var showStrokeWidthChooser by remember { mutableStateOf(false) }
    var inViewMode by remember { mutableStateOf(false) }

    if (!inViewMode) {
        DrawingBoard(paths, { newPaths -> paths = newPaths }, strokeColor, strokeWidth)
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

@Composable
fun DisplayBoard(
    paths: List<DrawingPath>,
    onEditRequested: () -> Unit,
) {
    var playAnimation by remember { mutableStateOf(true) }
    var currentSegment by remember { mutableStateOf(0) }
    var currentDrawingPath by remember { mutableStateOf(0) }
    var partialPathLists: List<Path> by remember() { mutableStateOf(listOf(Path())) }
    LaunchedEffect(key1 = playAnimation) {
        partialPathLists = listOf(Path())
        while (playAnimation) {
            delay(100)
            val currentPath = paths[currentDrawingPath]
            if (currentSegment == 0) {
                partialPathLists.last()
                    .moveTo(currentPath.pointXs.first(), currentPath.pointYs.first())
                currentSegment += 1
            } else if (currentSegment == currentPath.pointsNum-1) {
                partialPathLists.last()
                    .lineTo(currentPath.pointXs.last(), currentPath.pointYs.last())
                currentSegment = 0
                currentDrawingPath += 1
                partialPathLists += Path()
            } else {
                val (fl1, fl2) = Pair(
                    currentPath.pointXs[currentSegment - 1],
                    currentPath.pointYs[currentSegment - 1]
                )
                val x1 = (currentPath.pointXs[currentSegment] + fl1) / 2
                val x2 = (currentPath.pointYs[currentSegment] + fl2) / 2
                partialPathLists.last().quadraticBezierTo(fl1, fl2, x1, x2)
                currentSegment += 1
            }
            if (currentDrawingPath >= paths.size)
                playAnimation = false
            Log.d("DrawingBoard", "Redraw")
        }
    }
    Canvas(modifier = Modifier
        .fillMaxSize()
        .clickable { onEditRequested() }) {
        for (i in 0.. currentDrawingPath) {
            drawPath(
                color = Color(paths[i].strokeColor),
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

@Composable
fun DrawingBoard(
    paths: List<DrawingPath>,
    onPathsChanged: (List<DrawingPath>) -> Unit,
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