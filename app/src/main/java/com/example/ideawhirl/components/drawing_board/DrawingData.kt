package com.example.ideawhirl.components.drawing_board

import kotlinx.serialization.Serializable

@Serializable
data class DrawingData(
    val paths: List<Stroke>,
) {
    override fun equals(other: Any?): Boolean {
        return paths == (other as DrawingData).paths
    }
}