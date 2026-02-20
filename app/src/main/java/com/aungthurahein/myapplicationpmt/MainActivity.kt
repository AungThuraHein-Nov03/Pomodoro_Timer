package com.aungthurahein.myapplicationpmt

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.aungthurahein.myapplicationpmt.databinding.ActivityMainBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var dbHelper: SessionDatabaseHelper

    private var timer: CountDownTimer? = null
    private var isRunning = false
    private var isWorkSession = true
    private var workSeconds = Constants.DEFAULT_WORK_SECONDS
    private var shortBreakSeconds = Constants.DEFAULT_SHORT_BREAK_SECONDS
    private var longBreakSeconds = Constants.DEFAULT_LONG_BREAK_SECONDS
    private var breakSeconds = Constants.DEFAULT_SHORT_BREAK_SECONDS
    private var remainingSeconds = 0L
    private var workCount = 0L
    private var todaySessions = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
        
        prefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        dbHelper = SessionDatabaseHelper(this)
        loadStreak()
        updateDisplay()
        showRandomQuote()
    }

    private fun setupButtons() {
        binding.startButton.setOnClickListener { startTimer() }
        binding.pauseButton.setOnClickListener { pauseTimer() }
        binding.resetButton.setOnClickListener {
            if (isRunning || remainingSeconds > 0) {
                AlertDialog.Builder(this)
                    .setTitle("Reset Timer?")
                    .setMessage("Your current session will be lost.")
                    .setPositiveButton("Reset") { _, _ -> resetTimer() }
                    .setNegativeButton("Cancel", null)
                    .show()
            } else {
                resetTimer()
            }
        }
        binding.statsButton.setOnClickListener {
            startActivity(Intent(this, StatisticsActivity::class.java))
        }
        
        // Work Presets
        binding.presetWork15.setOnClickListener { applyWorkPreset(15L) }
        binding.presetWork25.setOnClickListener { applyWorkPreset(25L) }
        binding.presetWork45.setOnClickListener { applyWorkPreset(45L) }
        binding.presetWork90.setOnClickListener { applyWorkPreset(90L) }
        
        // Break Presets
        binding.presetBreakShort.setOnClickListener { applyBreakPreset(5L) }
        binding.presetBreakLong.setOnClickListener { applyBreakPreset(15L) }
    }
    
    private fun applyWorkPreset(seconds: Long) {
        workSeconds = seconds
        Toast.makeText(this, getString(R.string.work_time_set, seconds.toInt()), Toast.LENGTH_SHORT).show()
        if (!isRunning && isWorkSession) updateDisplay()
    }
    
    private fun applyBreakPreset(seconds: Long) {
        shortBreakSeconds = seconds
        Toast.makeText(this, getString(R.string.break_time_set, seconds.toInt()), Toast.LENGTH_SHORT).show()
        
        if (workCount % Constants.SESSIONS_FOR_LONG_BREAK != 0L) {
            breakSeconds = shortBreakSeconds
        }
        if (!isRunning && !isWorkSession) {
             updateTimer(breakSeconds)
             binding.linearProgressBar.max = breakSeconds.toInt()
             binding.linearProgressBar.progress = 0
        }
        updateSettingsSummary()
    }

    override fun onResume() {
        super.onResume()
        loadStreak()

        if (!isRunning && binding.taskNameInput.text?.isNotEmpty() == true) {
            binding.taskNameInput.text?.clear()
        }

        // Force reset to work mode if returning from break (and not running)
        if (!isRunning && !isWorkSession) {
            isWorkSession = true
            updateDisplay()
            showRandomQuote()
            binding.startButton.visibility = View.VISIBLE
            binding.pauseButton.visibility = View.GONE
            Toast.makeText(this, getString(R.string.back_to_work_mode), Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTimer() {
        if (isRunning) return
        val totalSeconds = if (isWorkSession) workSeconds else breakSeconds
        val startFrom = if (remainingSeconds > 0) remainingSeconds else totalSeconds
        binding.linearProgressBar.max = totalSeconds.toInt()
        binding.linearProgressBar.progress = (totalSeconds - startFrom).toInt()
        timer = object : CountDownTimer(startFrom * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                remainingSeconds = secondsLeft
                updateTimer(secondsLeft)
                val progress = (totalSeconds - secondsLeft).toInt()
                binding.linearProgressBar.progress = progress
            }

            override fun onFinish() {
                isRunning = false
                remainingSeconds = 0
                binding.startButton.visibility = View.VISIBLE
                binding.pauseButton.visibility = View.GONE
                setPresetsEnabled(true)
                binding.timerText.text = getString(R.string.timer_zero)
                binding.linearProgressBar.progress = binding.linearProgressBar.max

                if (isWorkSession) {
                    workCount++

                    val taskName = binding.taskNameInput.text.toString().trim()
                    val durationSeconds = workSeconds.toInt()
                    val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                    dbHelper.insertSession(
                        SessionRecord(
                            date = today,
                            taskName = taskName.ifEmpty { getString(R.string.default_task_name) },
                            durationMinutes = durationSeconds
                        )
                    )
                    loadStreak()
                }

                isWorkSession = !isWorkSession
                if (!isWorkSession) {
                    breakSeconds = if (workCount % Constants.SESSIONS_FOR_LONG_BREAK == 0L) longBreakSeconds else shortBreakSeconds
                }

                val sessionType = if (isWorkSession) "Work" else "Break"
                val isLongBreak = !isWorkSession && (workCount % Constants.SESSIONS_FOR_LONG_BREAK == 0L)
                showStatus("${if (isLongBreak) "Long " else ""}$sessionType time! Take a ${if (isWorkSession) "break" else "victory lap"}.")
                Toast.makeText(this@MainActivity, "Time's up! ${if (isLongBreak) "Long " else ""}$sessionType incoming.", Toast.LENGTH_LONG).show()

                val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val ringtone = RingtoneManager.getRingtone(this@MainActivity, notification)
                ringtone?.play()

                showRandomQuote()

                if (!isWorkSession) {
                    val intent = Intent(this@MainActivity, BreakActivity::class.java)
                    intent.putExtra("break_seconds", breakSeconds)
                    startActivity(intent)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }
            }
        }.start()

        isRunning = true
        binding.startButton.visibility = View.GONE
        binding.pauseButton.visibility = View.VISIBLE
        setPresetsEnabled(false)
        showStatus(if (isWorkSession) getString(R.string.work_in_progress) else getString(R.string.break_in_progress))
    }

    private fun pauseTimer() {
        timer?.cancel()
        isRunning = false
        binding.startButton.visibility = View.VISIBLE
        binding.pauseButton.visibility = View.GONE
        setPresetsEnabled(true)
        showStatus(if (isWorkSession) getString(R.string.work_paused) else getString(R.string.break_paused))
    }

    private fun resetTimer() {
        timer?.cancel()
        isRunning = false
        remainingSeconds = 0
        isWorkSession = true
        workCount = 0L
        binding.startButton.visibility = View.VISIBLE
        binding.pauseButton.visibility = View.GONE
        setPresetsEnabled(true)
        updateDisplay()
        showRandomQuote()
    }

    private fun setPresetsEnabled(enabled: Boolean) {
        val alpha = if (enabled) 1.0f else 0.4f
        binding.presetWork15.isEnabled = enabled
        binding.presetWork25.isEnabled = enabled
        binding.presetWork45.isEnabled = enabled
        binding.presetWork90.isEnabled = enabled
        binding.presetBreakShort.isEnabled = enabled
        binding.presetBreakLong.isEnabled = enabled
        binding.presetWork15.alpha = alpha
        binding.presetWork25.alpha = alpha
        binding.presetWork45.alpha = alpha
        binding.presetWork90.alpha = alpha
        binding.presetBreakShort.alpha = alpha
        binding.presetBreakLong.alpha = alpha
    }

    private fun updateTimer(seconds: Long) {
        val mins = seconds / 60
        val secs = seconds % 60
        binding.timerText.text = String.format("%02d:%02d", mins, secs)
    }

    private fun updateDisplay() {
        updateTimer(workSeconds)
        binding.linearProgressBar.max = workSeconds.toInt()
        binding.linearProgressBar.progress = 0
        updateSettingsSummary()
    }

    private fun updateSettingsSummary() {
        val workSecs = workSeconds
        val breakSecs = shortBreakSeconds
        showStatus(getString(R.string.settings_summary, workSecs, breakSecs))
    }

    private fun loadStreak() {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        todaySessions = dbHelper.getSessionsForDate(today)
        workCount = todaySessions.toLong()
        updateStreakDisplay()
    }

    private fun updateStreakDisplay() {
        binding.streakText.text = getString(R.string.sessions_today, todaySessions)
        val tintColor = if (todaySessions >= 1) R.color.break_primary else R.color.text_secondary
        binding.checkIcon.setColorFilter(getColor(tintColor))
    }

    private fun saveStreak() {
        // Session count is now derived from the database, no SharedPreferences save needed
    }

    private fun showRandomQuote() {
        val quote = MotivationalQuotes.getRandom()
        binding.quoteText.text = "\u201C${quote.text}\u201D"
        binding.quoteAuthor.text = "\u2014 ${quote.author}"
        binding.quoteText.visibility = View.VISIBLE
        binding.quoteAuthor.visibility = View.VISIBLE
        binding.statusText.visibility = View.GONE
    }

    private fun showStatus(text: String) {
        binding.statusText.text = text
        binding.statusText.visibility = View.VISIBLE
        binding.quoteText.visibility = View.GONE
        binding.quoteAuthor.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}