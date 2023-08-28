package com.example.ideawhirl.model

data class Note(
    val name: String,
    val detail: String,
    val tag: List<String>,
    val uid: Int = 0,
)
