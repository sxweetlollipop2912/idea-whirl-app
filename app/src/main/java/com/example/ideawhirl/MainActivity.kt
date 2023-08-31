package com.example.ideawhirl

import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import com.example.ideawhirl.data.data_source.LocalDatabase
import com.example.ideawhirl.data.repo.NoteRepo
import com.example.ideawhirl.ui.home.HomeRoute
import com.example.ideawhirl.ui.home.HomeViewModel
import com.example.ideawhirl.ui.theme.IdeaWhirlTheme


class MainActivity : ComponentActivity() {
    private lateinit var mSensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeRoute(
                homeViewModel = HomeViewModel(
                    NoteRepo(
                        Room.inMemoryDatabaseBuilder(
                            this,
                            LocalDatabase::class.java,
                        ).fallbackToDestructiveMigration().build()
                    )
                ),
                onToNote = { /*TODO*/ },
                onToNoteList = { /*TODO*/ },
                onToSettings = { /*TODO*/ },
                sensorManager = mSensorManager,
            )
        }

        // Getting the Sensor Manager instance
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
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
