package com.example.ideawhirl

import ShakeEventListener
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ideawhirl.ui.theme.IdeaWhirlTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val animatableRotation = remember { Animatable(0f) }
            val scope = rememberCoroutineScope()
            ShakeEventListener(this, mSensorManager) {
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
                    Column {
                        Greeting("Android")
                        AnimationBox(animatableRotation)
                    }
                }
            }
        }

        // Getting the Sensor Manager instance
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IdeaWhirlTheme {
        Greeting("Android")
    }
}

@Composable
fun AnimationBox(animatableRotation: Animatable<Float, AnimationVector1D>) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(250.dp)) {
            rotate(animatableRotation.value) {
                drawRect(color = Color(50, 205, 50))
            }
        }
    }
}
