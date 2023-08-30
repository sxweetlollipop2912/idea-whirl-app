package com.example.ideawhirl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.ideawhirl.ui.navigation.NavRoutes
import com.example.ideawhirl.ui.navigation.NoteRepo
import com.example.ideawhirl.ui.navigation.ThisNavGraph
import com.example.ideawhirl.ui.navigation.rememberThisNavController
import com.example.ideawhirl.ui.theme.IdeaWhirlTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IdeaWhirlTheme {
                val thisNavController = rememberThisNavController()

                ThisNavGraph(
                    repository = NoteRepo(),
                    thisNavController = thisNavController,
                    startDestination = NavRoutes.HOME.route,
                )
            }
        }
    }
}