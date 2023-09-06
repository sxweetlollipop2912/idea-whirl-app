package com.example.ideawhirl.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

private val buttonSize: Dp = 42.dp
private val sizeSelected: Dp = 30.dp
private val sizeUnselected: Dp = 24.dp

@Composable
fun ColorPickerPopup(
    selectedColor: Color,
    colors: List<Color>,
    onColorSelected: (Color) -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Popup(
        onDismissRequest = { expanded = false },
        alignment = Alignment.TopStart,
    ) {
        Column(
            modifier = modifier
                .width(buttonSize)
                .background(backgroundColor, CircleShape)
                .animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (!expanded) {
                Box(
                    modifier = Modifier
                        .size(buttonSize)
                        .clickable { expanded = true }
                        .background(Color.Transparent, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(sizeSelected)
                            .background(selectedColor, CircleShape)
                    )
                }
            } else {
                key("selected") {
                    Box(
                        modifier = Modifier
                            .size(buttonSize)
                            .background(backgroundColor, CircleShape)
                            .clickable { expanded = false },
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(sizeSelected)
                                .background(selectedColor, CircleShape)
                        )
                    }
                }
                Spacer(Modifier.height(5.dp))
                for (color in colors.filter { it != selectedColor }) {
                    key(color) {
                        FloatingActionButton(
                            onClick = {
                                onColorSelected(color)
                            },
                            shape = CircleShape,
                            containerColor = color,
                            modifier = Modifier.size(sizeUnselected),
                        ) {}
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}