package com.example.ideawhirl

import androidx.compose.ui.graphics.Color
import com.example.ideawhirl.components.drawing_board.DrawingPath
import com.example.ideawhirl.components.drawing_board.StrokeWidth
import junit.framework.TestCase
import org.junit.Test

class DrawingPathTest {
    @Test
    fun saveCorrectly() {
        val path = DrawingPath(StrokeWidth.Normal, Color.DarkGray)
        val points = listOf(
            Pair(1f, 2f),
            Pair(4f, 5f),
            Pair(4f, 5f),
            Pair(7f, 8f),
            Pair(4f, 12f),
            Pair(53f, 24f),
            Pair(12f, 54f),
        )
        path.start(points[0].first, points[0].second)
        path.drawTo(points[1].first, points[1].second)
        path.drawTo(points[2].first, points[2].second)
        path.drawTo(points[3].first, points[3].second)
        path.drawTo(points[4].first, points[4].second)
        path.drawTo(points[5].first, points[5].second)
        path.finish(points[6].first, points[6].second)
        TestCase.assertEquals(points, path.points)
    }
}