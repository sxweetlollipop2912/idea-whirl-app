package com.example.ideawhirl.model

import java.util.Date

data class Note(
    val name: String,
    val detail: String,
    val tag: List<String>,
    val uid: Int = 0,
    val createdAt: Date? = null,
)
