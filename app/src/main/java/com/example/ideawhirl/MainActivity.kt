package com.example.ideawhirl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.ideawhirl.data.data_source.LocalDatabase
import com.example.ideawhirl.data.repo.NoteRepo
import com.example.ideawhirl.ui.note_list.NoteListRoute
import com.example.ideawhirl.ui.note_list.NoteListViewModel
import com.example.ideawhirl.ui.theme.IdeaWhirlTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.inMemoryDatabaseBuilder(
            applicationContext,
            LocalDatabase::class.java,
        ).fallbackToDestructiveMigration().build()
        val noteRepo = NoteRepo(db)

        setContent {
            IdeaWhirlTheme {
                val noteListViewModel: NoteListViewModel = viewModel(
                    factory = NoteListViewModel.provideFactory(
                        repository = noteRepo,
                    )
                )
                // A surface container using the 'background' color from the theme
                NoteListRoute(
                    noteListViewModel = noteListViewModel,
                    onToNote = {},
                    onToCreateNote = {},
                )
            }
        }
    }
}