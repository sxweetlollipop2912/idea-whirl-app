package com.example.ideawhirl.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ideawhirl.data.repo.NoteRepo
import com.example.ideawhirl.model.Note
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.toList

@OptIn(FlowPreview::class)
suspend fun <T> Flow<List<T>>.flattenToList() =
    flatMapConcat { it.asFlow() }.toList()

class HomeViewModel(
    private val repository: NoteRepo
) : ViewModel() {

    val notes = repository.getAll()

    suspend fun getRandomNote(): Note {
        return notes.flattenToList().random()
    }

    companion object {
        fun provideFactory(
            repository: NoteRepo
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(repository) as T
            }
        }
    }
}