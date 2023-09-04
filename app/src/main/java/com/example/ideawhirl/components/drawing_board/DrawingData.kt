package com.example.ideawhirl.components.drawing_board

import kotlinx.serialization.Serializable

@Serializable
data class DrawingData(
    val paths: List<Stroke>,
) {
    override fun equals(other: Any?): Boolean {
        if (other as DrawingData == null) {
            return false
        }
        return paths == other.paths
    }
}