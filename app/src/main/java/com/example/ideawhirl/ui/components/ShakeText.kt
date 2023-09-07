package com.example.ideawhirl.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.example.ideawhirl.ui.theme.NotePalette

@Composable
fun ShakeText() {
    val text = "shake !!"
    val colors = NotePalette.values().filter { it.id != 0 }.map { listOf(it.main).random() }
    val colorText = buildAnnotatedString {
        text.forEachIndexed { index, c ->
            withStyle(style = SpanStyle(color = colors[index % colors.size])) {
                append(c)
            }
        }
    }
    Text(
        text = colorText,
        style = MaterialTheme.typography.displayMedium.copy(
            fontWeight = FontWeight.Light,
            letterSpacing = 3.sp,
        )
    )
}