package com.example.ideawhirl.model

import android.content.Context
import com.example.ideawhirl.components.drawing_board.DrawingData
import kotlinx.serialization.json.Json

data class Note(
    val name: String,
    val detail: String,
    val tag: List<String>,
    val uid: Int = 0,
    private val context: Context,
) {
    private var _drawingData: DrawingData? = null

    var drawingData: DrawingData
        get() {
            if (_drawingData == null) {
                return loadOrCreateDrawingData()
            }
            return _drawingData
                ?: throw AssertionError("Set to null after initialized by another thread")
        }
        set(value) {
            _drawingData = value
        }

    private fun loadOrCreateDrawingData(): DrawingData {
        val filename = "drawing_$uid.data"
        try {
            context.openFileInput(filename).bufferedReader().useLines { lines ->
                val content = lines.fold("") { content, text ->
                    content.plus(text)
                }
                return Json.decodeFromString(content)
            }
        } catch (e: Throwable) {
            return DrawingData(listOf())
        }
    }

}
