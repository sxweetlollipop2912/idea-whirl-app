package com.example.ideawhirl.ui

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("dd MMM yy | HH:mm", Locale.getDefault())
    return formatter.format(date)
}