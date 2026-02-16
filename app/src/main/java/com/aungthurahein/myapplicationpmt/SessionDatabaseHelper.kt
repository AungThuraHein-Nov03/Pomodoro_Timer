package com.aungthurahein.myapplicationpmt

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SessionDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "pomodoro_sessions.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_SESSIONS = "sessions"
        private const val COL_ID = "id"
        private const val COL_DATE = "date"
        private const val COL_TASK_NAME = "task_name"
        private const val COL_DURATION = "duration_minutes"
        private const val COL_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE $TABLE_SESSIONS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_DATE TEXT NOT NULL,
                $COL_TASK_NAME TEXT NOT NULL,
                $COL_DURATION INTEGER NOT NULL,
                $COL_TIMESTAMP INTEGER NOT NULL
            )"""
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SESSIONS")
        onCreate(db)
    }

    fun insertSession(session: SessionRecord): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_DATE, session.date)
            put(COL_TASK_NAME, session.taskName)
            put(COL_DURATION, session.durationMinutes)
            put(COL_TIMESTAMP, session.timestamp)
        }
        return db.insert(TABLE_SESSIONS, null, values)
    }

    fun getTotalWorkMinutes(): Int {
        val db = readableDatabase
        return db.rawQuery("SELECT COALESCE(SUM($COL_DURATION), 0) FROM $TABLE_SESSIONS", null).use { cursor ->
            cursor.moveToFirst()
            cursor.getInt(0)
        }
    }

    fun getTotalSessions(): Int {
        val db = readableDatabase
        return db.rawQuery("SELECT COUNT(*) FROM $TABLE_SESSIONS", null).use { cursor ->
            cursor.moveToFirst()
            cursor.getInt(0)
        }
    }

    fun getDailyMinutes(days: Int): List<Pair<String, Int>> {
        val db = readableDatabase
        val today = LocalDate.now()
        val startDate = today.minusDays((days - 1).toLong())
        val dateFormat = DateTimeFormatter.ISO_LOCAL_DATE
        val displayFormat = DateTimeFormatter.ofPattern("MM/dd")

        val dataMap = mutableMapOf<String, Int>()
        db.rawQuery(
            """SELECT $COL_DATE, SUM($COL_DURATION) as total
               FROM $TABLE_SESSIONS
               WHERE $COL_DATE >= ?
               GROUP BY $COL_DATE
               ORDER BY $COL_DATE ASC""",
            arrayOf(startDate.format(dateFormat))
        ).use { cursor ->
            while (cursor.moveToNext()) {
                dataMap[cursor.getString(0)] = cursor.getInt(1)
            }
        }

        val result = mutableListOf<Pair<String, Int>>()
        for (i in 0 until days) {
            val date = startDate.plusDays(i.toLong())
            val isoDate = date.format(dateFormat)
            val label = date.format(displayFormat)
            result.add(label to (dataMap[isoDate] ?: 0))
        }
        return result
    }

    fun getBestStreakDays(): Int {
        val db = readableDatabase
        val dates = db.rawQuery(
            "SELECT DISTINCT $COL_DATE FROM $TABLE_SESSIONS ORDER BY $COL_DATE ASC",
            null
        ).use { cursor ->
            val list = mutableListOf<LocalDate>()
            while (cursor.moveToNext()) {
                list.add(LocalDate.parse(cursor.getString(0)))
            }
            list
        }

        if (dates.isEmpty()) return 0

        var bestStreak = 1
        var currentStreak = 1
        for (i in 1 until dates.size) {
            if (dates[i] == dates[i - 1].plusDays(1)) {
                currentStreak++
                if (currentStreak > bestStreak) bestStreak = currentStreak
            } else {
                currentStreak = 1
            }
        }
        return bestStreak
    }

    fun getCurrentStreak(): Int {
        val db = readableDatabase
        val dates = db.rawQuery(
            "SELECT DISTINCT $COL_DATE FROM $TABLE_SESSIONS ORDER BY $COL_DATE DESC",
            null
        ).use { cursor ->
            val list = mutableListOf<LocalDate>()
            while (cursor.moveToNext()) {
                list.add(LocalDate.parse(cursor.getString(0)))
            }
            list
        }

        if (dates.isEmpty()) return 0

        val today = LocalDate.now()
        if (dates[0] != today && dates[0] != today.minusDays(1)) return 0

        var streak = 1
        for (i in 1 until dates.size) {
            if (dates[i] == dates[i - 1].minusDays(1)) {
                streak++
            } else {
                break
            }
        }
        return streak
    }

    fun getRecentSessions(limit: Int = 20): List<SessionRecord> {
        val db = readableDatabase
        return db.rawQuery(
            """SELECT $COL_ID, $COL_DATE, $COL_TASK_NAME, $COL_DURATION, $COL_TIMESTAMP
               FROM $TABLE_SESSIONS
               ORDER BY $COL_TIMESTAMP DESC
               LIMIT ?""",
            arrayOf(limit.toString())
        ).use { cursor ->
            val sessions = mutableListOf<SessionRecord>()
            while (cursor.moveToNext()) {
                sessions.add(
                    SessionRecord(
                        id = cursor.getLong(0),
                        date = cursor.getString(1),
                        taskName = cursor.getString(2),
                        durationMinutes = cursor.getInt(3),
                        timestamp = cursor.getLong(4)
                    )
                )
            }
            sessions
        }
    }

    fun getAverageDailyMinutes(days: Int = 7): Int {
        val daily = getDailyMinutes(days)
        val activeDays = daily.count { it.second > 0 }
        if (activeDays == 0) return 0
        val totalMinutes = daily.sumOf { it.second }
        return totalMinutes / activeDays
    }
}
