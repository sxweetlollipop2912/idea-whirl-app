package com.example.ideawhirl.model

data class Note(
    val uid: Int,
    val name: String,
    val detail: String,
    val tag: List<String>,
)
