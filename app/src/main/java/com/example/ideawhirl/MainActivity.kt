package com.example.ideawhirl

import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
        setContent {
            IdeaWhirlTheme {
                val thisNavController = rememberThisNavController()

                val db = Room.inMemoryDatabaseBuilder(
                    this,
                    LocalDatabase::class.java,
                ).fallbackToDestructiveMigration().build()
                val noteRepo = NoteRepo(db)

                ThisNavGraph(
                    repository = noteRepo,
                    thisNavController = thisNavController,
                    startDestination = NavRoutes.HOME.route,
                    sensorManager = mSensorManager
                )
            }
        }

        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
    }
}
