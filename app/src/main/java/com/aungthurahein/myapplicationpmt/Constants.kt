package com.aungthurahein.myapplicationpmt

object Constants {

    // Timer Defaults (in seconds, for demo)
    const val DEFAULT_WORK_SECONDS = 25L
    const val DEFAULT_SHORT_BREAK_SECONDS = 5L
    const val DEFAULT_LONG_BREAK_SECONDS = 15L

    // Timer Conversion: minutes to seconds
    fun minutesToSeconds(minutes: Long): Long = minutes * 60L

    // Long break after this many work sessions
    const val SESSIONS_FOR_LONG_BREAK = 4L

    // Timer Presets (in seconds)
    val WORK_PRESETS = listOf(15L, 25L, 45L, 90L)
    val BREAK_PRESETS = listOf(5L, 15L)

    // SharedPreferences Keys
    const val PREFS_NAME = "pomodoro_prefs"
    const val KEY_LAST_DATE = "last_date"
    const val KEY_SESSIONS_TODAY = "sessions_today"

    // Error Messages
    const val ERROR_INVALID_WORK_TIME = "Please enter a time between 1-120 seconds"
    const val ERROR_INVALID_BREAK_TIME = "Please enter a time between 1-60 seconds"
}
