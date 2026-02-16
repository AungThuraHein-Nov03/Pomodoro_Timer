package com.aungthurahein.myapplicationpmt

data class SessionRecord(
    val id: Long = 0,
    val date: String,
    val taskName: String,
    val durationMinutes: Int,
    val timestamp: Long = System.currentTimeMillis()
)
