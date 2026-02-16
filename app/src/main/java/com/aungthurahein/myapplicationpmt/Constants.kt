package com.aungthurahein.myapplicationpmt

object Constants {

    // Timer Defaults (in minutes)
    const val DEFAULT_WORK_MINUTES = 25L
    const val DEFAULT_SHORT_BREAK_MINUTES = 5L
    const val DEFAULT_LONG_BREAK_MINUTES = 15L

    // Timer Conversion: minutes to seconds
    fun minutesToSeconds(minutes: Long): Long = minutes * 60L

    // Long break after this many work sessions
    const val SESSIONS_FOR_LONG_BREAK = 4L

    // Input Validation Limits (in minutes)
    const val MIN_WORK_MINUTES = 1L
    const val MAX_WORK_MINUTES = 120L

    const val MIN_BREAK_MINUTES = 1L
    const val MAX_BREAK_MINUTES = 60L

    // SharedPreferences Keys
    const val PREFS_NAME = "pomodoro_prefs"
    const val KEY_TASK_NAME = "task_name"
    const val KEY_LAST_DATE = "last_date"
    const val KEY_SESSIONS_TODAY = "sessions_today"

    // Error Messages
    const val ERROR_INVALID_WORK_TIME = "Please enter a time between 1-120 minutes"
    const val ERROR_INVALID_BREAK_TIME = "Please enter a time between 1-60 minutes"
    
    // Timer Presets (in minutes)
    val WORK_PRESETS = listOf(15L, 25L, 45L, 90L)
    val BREAK_PRESETS = listOf(5L, 15L)
}
