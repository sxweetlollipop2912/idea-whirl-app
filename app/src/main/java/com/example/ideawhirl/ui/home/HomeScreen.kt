package com.example.ideawhirl.ui.home

import ShakeEventListener
import android.graphics.RectF
import android.hardware.SensorManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import com.example.ideawhirl.Greeting
import com.example.ideawhirl.R
import com.example.ideawhirl.model.Note
import com.example.ideawhirl.ui.theme.IdeaWhirlTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onToNote: (String) -> Unit,
    onToNoteList: () -> Unit,
    onToSettings: () -> Unit,
    onShake: suspend () -> Note?,
    onStopDrag: suspend () -> Note?,
    sensorManager: SensorManager,
    modifier: Modifier = Modifier,
) {
    val animatableRotation = remember { Animatable(0f) }
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
        }
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
                Column {
                    Greeting("Android")
                    AnimationBox(
                        animatableRotation,
                        onStopDrag = onStopDrag
                    )
                }
            }
        }
    }
}


@Composable
fun AnimationBox(
    animatableRotation: Animatable<Float, AnimationVector1D>,
    onStopDrag: suspend () -> Note?,
) {
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val image = ImageBitmap.imageResource(R.drawable.box)

        DragTarget(onStartDrag = { /*TODO*/ }, onStopDrag = { /*TODO*/ }){
            Canvas(modifier = Modifier.size(250.dp)) {
                rotate(animatableRotation.value) {
                    drawIntoCanvas { canvas ->
                        canvas.nativeCanvas.drawBitmap(
                            image.asAndroidBitmap(),
                            null,
                            RectF(0f, 0f, 250 * density, 250 * density),
                            null
                        )
                    }
                }
            }
        }
    }
}
