package com.example.ideawhirl.components.drawing_board

import kotlinx.serialization.Serializable

@Serializable
data class DrawingData(
    val paths: List<Stroke>,
)