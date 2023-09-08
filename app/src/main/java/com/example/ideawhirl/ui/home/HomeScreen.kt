package com.example.ideawhirl.ui.home

import android.hardware.SensorManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimatable
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.ideawhirl.R
import com.example.ideawhirl.ShakeEventListener
import com.example.ideawhirl.model.Note
import com.example.ideawhirl.ui.components.ShakeText
import com.example.ideawhirl.ui.notelist.NoteListItemPreview
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onToNote: (Int) -> Unit,
    onToCreateNote: () -> Unit,
    onToNoteList: () -> Unit,
    tags: List<String>,
    getRandomNote: () -> Note?,
    getRandomNoteWithTag: (tag: String) -> Note?,
    sensorManager: SensorManager,
    modifier: Modifier = Modifier,
) {
    val animatableRotation = remember { Animatable(0f) }
    var currentTag by remember { mutableStateOf("") }
    var showTagsDialog by remember { mutableStateOf(false) }
    var showNote by remember { mutableStateOf(false) }
    var note by remember { mutableStateOf<Note?>(null) }
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.box_anim)
    )
    val animatableBox = rememberLottieAnimatable()
    val scope = rememberCoroutineScope()
    ShakeEventListener(LocalLifecycleOwner.current, sensorManager) {
        if (showNote) return@ShakeEventListener
        scope.launch {
            animatableRotation.animateTo(
                targetValue = -15f,
                animationSpec = tween(50, easing = EaseInOut),
            )
            animatableRotation.animateTo(
                targetValue = 15f,
                animationSpec = repeatable(
                    iterations = 9,
                    animation = tween(100, easing = EaseInOut),
                    repeatMode = RepeatMode.Reverse
                )
            )
            animatableRotation.animateTo(
                targetValue = 0f,
                animationSpec = tween(50, easing = EaseInOut),
            )
            animatableBox.animate(
                composition = composition,
                iterations = 1,
                speed = 1.5f,
                clipSpec = LottieClipSpec.Progress(0f, 0.5f),
            )
            showNote = true
            note = if (currentTag != "") getRandomNoteWithTag(currentTag) else getRandomNote()
            animatableBox.animate(
                composition = composition,
                iterations = 1,
                speed = 1.5f,
                clipSpec = LottieClipSpec.Progress(0.5f, 1f),
            )
        }
    }
    if (showNote) {
        Dialog(
            onDismissRequest = { showNote = false }
        ) {
            if (note != null)
                Card(
                    modifier = Modifier
                        .wrapContentSize(),
                ) {
                    NoteListItemPreview(
                        modifier = Modifier.padding(16.dp),
                        onItemClick = { onToNote(note!!.uid) },
                        note = note!!
                    )
                }
            else
                Card(
                    modifier = Modifier
                        .wrapContentSize(),
                ) {
                    Text(
                        modifier = Modifier
                            .padding(16.dp),
                        text = "No notes found"
                    )
                }
        }
    }
    if (showTagsDialog) {
        TagsDialog(
            tags = tags,
            currentTag = currentTag,
            onSelect = { selected ->
                currentTag = selected
                showTagsDialog = false
            },
            onDismiss = {
                showTagsDialog = false
            }
        )
    }
    // A surface container using the 'background' color from the theme
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        DraggableScreen(
            modifier = Modifier.fillMaxSize(),
        ) {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = onToCreateNote,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add new idea")
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ShakeText()
                    AnimationBox(
                        modifier = Modifier
                            .size(400.dp),
                        animatableRotation = animatableRotation,
                        composition = composition,
                        animatableBox = animatableBox,
                        onPressBox = onToNoteList,
                        onStopDrag = getRandomNote
                    )
                    Tags(
                        currentTag = currentTag,
                        onRequestTags = {
                            showTagsDialog = true
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsDialog(
    tags: List<String>,
    currentTag: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var tag by remember { mutableStateOf(currentTag) }
    Dialog(onDismissRequest = onDismiss) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .clickable(
                        onClick = {
                            expanded = !expanded
                        }
                    ),
                readOnly = true,
                value = if (tag != "") tag else "All ideas",
                onValueChange = { },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    containerColor = Color.White,
                    selectionColors = TextSelectionColors(
                        handleColor = Color.Transparent,
                        backgroundColor = Color.Transparent,
                    ),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
            ExposedDropdownMenu(
                modifier = Modifier
                    .background(Color.White),
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                tags.forEach { item ->
                    DropdownMenuItem(
                        onClick = {
                            tag = item
                            onSelect(item)
                            expanded = false
                        },
                        text = {
                            Text(
                                text = item,
                                fontSize = 12.sp,
                                color = Color(0xFF001833),
                            )
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Tags(
    modifier: Modifier = Modifier,
    currentTag: String = "",
    onRequestTags: () -> Unit,
) {
    Card(
        modifier = modifier,
        shape = CircleShape,
        onClick = onRequestTags
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = if (currentTag != "") currentTag else "All ideas"
        )
    }
}

@Composable
fun AnimationBox(
    modifier: Modifier = Modifier,
    animatableRotation: Animatable<Float, AnimationVector1D>,
    composition: LottieComposition?,
    animatableBox: LottieAnimatable,
    onPressBox: () -> Unit,
    onStopDrag: suspend () -> Note?,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        DragTarget(onStartDrag = { /*TODO*/ }, onStopDrag = { /*TODO*/ }) {
            LottieAnimation(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(animatableRotation.value)
                    .clickable {
                        onPressBox()
                    },
                composition = composition,
                progress = animatableBox.progress,
                contentScale = ContentScale.Fit
            )
        }
    }
}
