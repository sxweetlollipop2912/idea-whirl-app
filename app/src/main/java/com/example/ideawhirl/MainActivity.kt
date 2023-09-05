package com.example.ideawhirl

import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.room.Room
import com.example.ideawhirl.data.data_source.LocalDatabase
import com.example.ideawhirl.data.repo.NoteRepo
import com.example.ideawhirl.ui.navigation.NavRoutes
import com.example.ideawhirl.ui.navigation.ThisNavGraph
import com.example.ideawhirl.ui.navigation.rememberThisNavController
import com.example.ideawhirl.ui.theme.IdeaWhirlTheme

class MainActivity : ComponentActivity() {
    private lateinit var mSensorManager: SensorManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // for adaptability when keyboard is open
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            IdeaWhirlTheme {
                val thisNavController = rememberThisNavController()

                val db = Room.inMemoryDatabaseBuilder(
                    this,
                    LocalDatabase::class.java,
                ).fallbackToDestructiveMigration().build()
                val noteRepo = NoteRepo(db, this)

                ThisNavGraph(
                    repository = noteRepo,
                    thisNavController = thisNavController,
                    startDestination = NavRoutes.NOTE_LIST.route,
                    sensorManager = mSensorManager,
                    modifier = Modifier.safeDrawingPadding()
                )
            }
        }

        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
    }
}
