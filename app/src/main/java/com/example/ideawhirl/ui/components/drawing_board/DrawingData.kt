package com.example.ideawhirl.ui.components.drawing_board

import kotlinx.serialization.Serializable

@Serializable
data class DrawingData(
    val paths: List<Stroke>,
    val canvasSizeX: Float?,
    val canvasSizeY: Float?,
) {
    override fun equals(other: Any?): Boolean {
        if (paths != (other as DrawingData).paths)
            return false
        if (canvasSizeX != other.canvasSizeX)
            return false
        if (canvasSizeY != other.canvasSizeY)
            return false
        return true
    }

    fun isEmpty(): Boolean {
        return paths.isEmpty()
    }

    fun availableForPreview(): Boolean {
        return canvasSizeX != null && canvasSizeY != null
    }
    companion object {
        fun emptyData() = DrawingData(emptyList(), null, null)
    }
}