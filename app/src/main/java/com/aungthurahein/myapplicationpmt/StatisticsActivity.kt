package com.aungthurahein.myapplicationpmt

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.aungthurahein.myapplicationpmt.databinding.ActivityStatisticsBinding
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class StatisticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticsBinding
    private lateinit var dbHelper: SessionDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = SessionDatabaseHelper(this)

        binding.backButton.setOnClickListener { finish() }

        loadStatistics()
    }

    private fun loadStatistics() {
        val totalMinutes = dbHelper.getTotalWorkMinutes()
        val totalSessions = dbHelper.getTotalSessions()
        val bestStreak = dbHelper.getBestStreakDays()
        val currentStreak = dbHelper.getCurrentStreak()
        val avgDaily = dbHelper.getAverageDailyMinutes(7)
        val dailyData = dbHelper.getDailyMinutes(7)
        val recentSessions = dbHelper.getRecentSessions(8)

        if (totalSessions == 0) {
            binding.emptyStateText.visibility = View.VISIBLE
        }

        val hours = totalMinutes / 60
        val mins = totalMinutes % 60
        binding.totalTimeValue.text = if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
        binding.totalSessionsValue.text = totalSessions.toString()
        binding.bestStreakValue.text = "$bestStreak day${if (bestStreak != 1) "s" else ""}"
        binding.currentStreakValue.text = "$currentStreak day${if (currentStreak != 1) "s" else ""}"
        binding.avgDailyValue.text = "${avgDaily}m"

        binding.dailyChart.setData(dailyData)

        populateTaskLog(recentSessions)
    }

    private fun populateTaskLog(sessions: List<SessionRecord>) {
        val container = binding.taskLogContainer
        container.removeAllViews()

        if (sessions.isEmpty()) return

        val dp = resources.displayMetrics.density
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
        val dateFormatter = DateTimeFormatter.ofPattern("MMM dd")

        for ((index, session) in sessions.withIndex()) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
                setPadding((14 * dp).toInt(), (12 * dp).toInt(), (14 * dp).toInt(), (12 * dp).toInt())
                if (index % 2 == 1) {
                    setBackgroundColor(getColor(R.color.background_light))
                }
            }

            val instant = Instant.ofEpochMilli(session.timestamp)
            val zoned = instant.atZone(ZoneId.systemDefault())

            val dateText = TextView(this).apply {
                text = zoned.toLocalDate().format(dateFormatter)
                setTextColor(getColor(R.color.text_primary))
                textSize = 13f
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.2f)
            }

            val timeText = TextView(this).apply {
                text = zoned.toLocalTime().format(timeFormatter)
                setTextColor(getColor(R.color.text_secondary))
                textSize = 13f
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val taskText = TextView(this).apply {
                text = session.taskName.ifEmpty { "Focus Session" }
                setTextColor(getColor(R.color.pomodoro_primary))
                textSize = 13f
                maxLines = 1
                ellipsize = android.text.TextUtils.TruncateAt.END
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.8f)
            }

            row.addView(dateText)
            row.addView(timeText)
            row.addView(taskText)
            container.addView(row)

            if (index < sessions.size - 1) {
                val divider = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, (1 * dp).toInt()
                    ).apply {
                        marginStart = (14 * dp).toInt()
                        marginEnd = (14 * dp).toInt()
                    }
                    setBackgroundColor(getColor(R.color.divider))
                }
                container.addView(divider)
            }
        }
    }
}
