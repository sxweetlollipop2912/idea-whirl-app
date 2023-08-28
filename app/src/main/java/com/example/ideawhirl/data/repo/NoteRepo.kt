package com.example.ideawhirl.data.repo

import com.example.ideawhirl.data.data_source.LocalDatabase
import com.example.ideawhirl.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepo(val database: LocalDatabase) {
//    fun getAllNotes(): Flow<List<Note>> {
//        val entites = database.noteDao().getAll();
//        val notes = entites.map {
//            it.map {
//                val note = Note(
//                    uid = it.uid,
//                    name = it.name,
//                    detail = it.detail,
//                    tag = listOf(),
//                )
//                note
//            }
//        }
//        return Flow(listOf());
//    }
}