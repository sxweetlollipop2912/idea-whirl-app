package com.example.ideawhirl.ui.home

import android.graphics.RectF
import android.hardware.SensorManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimatable
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCancellationBehavior
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.ideawhirl.R
import com.example.ideawhirl.ShakeEventListener
import com.example.ideawhirl.model.Note
import com.example.ideawhirl.ui.theme.IdeaWhirlTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onToNote: (String) -> Unit,
    onToNoteList: () -> Unit,
    onToSettings: () -> Unit,
    tags: Array<String>,
    getRandomNote: suspend () -> Note?,
    getRandomNoteWithTag: suspend (tag: String) -> Note?,
    sensorManager: SensorManager,
    modifier: Modifier = Modifier,
) {
    val animatableRotation = remember { Animatable(0f) }
    var currentTag by remember { mutableStateOf("") }
    var showTagsDialog by remember { mutableStateOf(false) }
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.box_anim)
    )
    val animatableBox = rememberLottieAnimatable()
    val scope = rememberCoroutineScope()
    ShakeEventListener(LocalLifecycleOwner.current, sensorManager) {
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
            )
        }
    }
    if (showTagsDialog) {
        TagsDialog(
            tags = tags,
            currentTag = currentTag,
            onSelect = {selected ->
                currentTag = selected
                showTagsDialog = false
            },
            onDismiss = {
                showTagsDialog = false
            }
        )
    }
    IdeaWhirlTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            DraggableScreen(
                modifier = Modifier.fillMaxSize(),
            ) {
                Scaffold(
                    topBar = {
                        TopBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(16.dp)
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = { /*TODO*/ }) {
                            Icon(Icons.Default.Add, contentDescription = "Add new idea")
                        }
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
//                            modifier = Modifier.padding(bottom = 32.dp),
                            text = "Shake your phone to get a random idea!",
                        )
                        AnimationBox(
                            modifier = Modifier
                                .size(250.dp),
                            animatableRotation = animatableRotation,
                            composition = composition,
                            animatableBox = animatableBox,
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .size(50.dp),
            shape = CircleShape,
            onClick = { /*TODO*/ },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = "User"
                )
            }
        }
        Card(
            modifier = Modifier
                .size(50.dp),
            shape = CircleShape,
            onClick = { /*TODO*/ }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.settings),
                    contentDescription = "Settings"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsDialog(
    tags: Array<String>,
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
                onValueChange = {  },
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
                        text = { Text(
                            text = item,
                            fontSize = 12.sp,
                            color = Color(0xFF001833),
                        ) },
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
    onStopDrag: suspend () -> Note?,
) {
//    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val image = ImageBitmap.imageResource(R.drawable.box)

        DragTarget(onStartDrag = { /*TODO*/ }, onStopDrag = { /*TODO*/ }) {
//            Canvas(modifier = Modifier.size(250.dp)) {
//                rotate(animatableRotation.value) {
//                    drawIntoCanvas { canvas ->
//                        canvas.nativeCanvas.drawBitmap(
//                            image.asAndroidBitmap(),
//                            null,
//                            RectF(0f, 0f, 250 * density, 250 * density),
//                            null
//                        )
//                    }
//                }
//            }
            LottieAnimation(
                modifier = Modifier
                    .size(250.dp)
                    .rotate(animatableRotation.value),
                composition = composition,
                progress = animatableBox.progress,
            )
        }
    }
}
