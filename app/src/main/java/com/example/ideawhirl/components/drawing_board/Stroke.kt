package com.example.ideawhirl.components.drawing_board

import androidx.compose.ui.graphics.Path
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class Stroke(
    val pointXs: MutableList<Float> = mutableListOf(),
    val pointYs: MutableList<Float> = mutableListOf(),
    @Transient var path: Path = Path()
) {
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

    abstract fun strokeWidth(): Float
    abstract fun strokeColorIndex(): Int?

    override fun equals(other: Any?): Boolean {
        if (other as Stroke == null) return false
        return pointXs == other.pointXs && pointYs == other.pointYs
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

@Serializable
class DrawingPath(
    private val strokeWidth: StrokeWidth,
    val strokeColorIndex: Int,
) : Stroke() {
    override fun strokeWidth(): Float {
        return strokeWidth.toFloat()
    }

    override fun strokeColorIndex(): Int? {
        return strokeColorIndex
    }

    override fun equals(other: Any?): Boolean {
        if (!super.equals(other)) return false
        if (other as DrawingPath == null) return false
        if (strokeColorIndex != other.strokeColorIndex) return false
        if (strokeWidth != other.strokeWidth) return false
        return true
    }
}

@Serializable
class EraserPath(
    private val eraserWidth: EraserWidth,
) : Stroke() {
    override fun strokeWidth(): Float {
        return eraserWidth.toFloat()
    }

    override fun equals(other: Any?): Boolean {
        if (!super.equals(other)) return false
        if (other as EraserPath == null) return false
        if (eraserWidth != other.eraserWidth) return false
        return true
    }

    override fun strokeColorIndex(): Int? {
        return null
    }
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
            Normal -> 4f
            Bold -> 6f
            Bolder -> 8f
        }
    }
}

enum class EraserWidth {
    Lighter,
    Light,
    Normal,
    Bold,
    Bolder;

    fun toFloat(): Float {
        return when (this) {
            Lighter -> 8f
            Light -> 10f
            Normal -> 15f
            Bold -> 18f
            Bolder -> 24f
        }
    }
}

