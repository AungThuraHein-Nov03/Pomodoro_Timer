package com.aungthurahein.myapplicationpmt

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
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
    private var workSeconds = Constants.minutesToSeconds(Constants.DEFAULT_WORK_MINUTES)
    private var shortBreakSeconds = Constants.minutesToSeconds(Constants.DEFAULT_SHORT_BREAK_MINUTES)
    private var longBreakSeconds = Constants.minutesToSeconds(Constants.DEFAULT_LONG_BREAK_MINUTES)
    private var breakSeconds = Constants.minutesToSeconds(Constants.DEFAULT_SHORT_BREAK_MINUTES)
    private var workCount = 0L
    private var todaySessions = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInputFields()
        setupButtons()
        
        prefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        dbHelper = SessionDatabaseHelper(this)
        loadStreak()
        loadTaskName()
        updateDisplay()
    }

    private fun setupButtons() {
        binding.startButton.setOnClickListener { startTimer() }
        binding.pauseButton.setOnClickListener { pauseTimer() }
        binding.resetButton.setOnClickListener { resetTimer() }
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
    
    private fun applyWorkPreset(minutes: Long) {
        if (minutes < Constants.MIN_WORK_MINUTES || minutes > Constants.MAX_WORK_MINUTES) return
        workSeconds = Constants.minutesToSeconds(minutes)
        // Visual feedback
        Toast.makeText(this, "Work time set to $minutes minutes", Toast.LENGTH_SHORT).show()
        if (!isRunning && isWorkSession) updateDisplay()
    }
    
    private fun applyBreakPreset(minutes: Long) {
        if (minutes < Constants.MIN_BREAK_MINUTES || minutes > Constants.MAX_BREAK_MINUTES) return
        shortBreakSeconds = Constants.minutesToSeconds(minutes)
        // Visual feedback
        Toast.makeText(this, "Break time set to $minutes minutes", Toast.LENGTH_SHORT).show()
        
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

    private fun setupInputFields() {
        binding.taskNameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                saveTaskName(s.toString())
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // Force reset to work mode if returning from break (and not running)
        if (!isRunning && !isWorkSession) {
            isWorkSession = true
            updateDisplay()
            binding.statusText.text = "Ready for work!"
            binding.startButton.visibility = View.VISIBLE
            binding.pauseButton.visibility = View.GONE
            Toast.makeText(this, "Back to work mode!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTimer() {
        if (isRunning) return
        val totalSeconds = if (isWorkSession) workSeconds else breakSeconds
        binding.linearProgressBar.max = totalSeconds.toInt()
        binding.linearProgressBar.progress = 0
        timer = object : CountDownTimer(totalSeconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                updateTimer(secondsLeft)
                val progress = (totalSeconds - secondsLeft).toInt()
                binding.linearProgressBar.progress = progress
            }

            override fun onFinish() {
                isRunning = false
                binding.startButton.visibility = View.VISIBLE
                binding.pauseButton.visibility = View.GONE
                binding.timerText.text = "00:00"
                binding.linearProgressBar.progress = binding.linearProgressBar.max


                if (isWorkSession) {
                    workCount++
                    todaySessions++
                    updateStreakDisplay()
                    saveStreak()

                    val taskName = binding.taskNameInput.text.toString().trim()
                    val durationMinutes = (workSeconds / 60).toInt()
                    val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                    dbHelper.insertSession(
                        SessionRecord(
                            date = today,
                            taskName = taskName.ifEmpty { "Focus Session" },
                            durationMinutes = durationMinutes
                        )
                    )
                }

                isWorkSession = !isWorkSession
                if (!isWorkSession) {
                    breakSeconds = if (workCount % Constants.SESSIONS_FOR_LONG_BREAK == 0L) longBreakSeconds else shortBreakSeconds
                }

                val sessionType = if (isWorkSession) "Work" else "Break"
                val isLongBreak = !isWorkSession && (workCount % Constants.SESSIONS_FOR_LONG_BREAK == 0L)
                binding.statusText.text = "${if (isLongBreak) "Long " else ""}$sessionType time! Take a ${if (isWorkSession) "break" else "victory lap"}."
                Toast.makeText(this@MainActivity, "Time's up! ${if (isLongBreak) "Long " else ""}$sessionType incoming.", Toast.LENGTH_LONG).show()

                val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val ringtone = RingtoneManager.getRingtone(this@MainActivity, notification)
                ringtone?.play()

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
        binding.statusText.text = if (isWorkSession) "Crush that work!" else "Chill out."
    }

    private fun pauseTimer() {
        timer?.cancel()
        isRunning = false
        binding.startButton.visibility = View.VISIBLE
        binding.pauseButton.visibility = View.GONE
        binding.statusText.text = if (isWorkSession) "Work paused. Back at it?" else "Break paused. You do you."
    }

    private fun resetTimer() {
        timer?.cancel()
        isRunning = false
        isWorkSession = true
        workCount = 0L
        binding.startButton.visibility = View.VISIBLE
        binding.pauseButton.visibility = View.GONE
        updateDisplay()
        binding.statusText.text = "Ready for work!"
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
        val workMins = workSeconds / 60
        val breakMins = shortBreakSeconds / 60
        binding.statusText.text = "Work: ${workMins}m • Break: ${breakMins}m"
    }

    private fun loadStreak() {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val savedDate = prefs.getString(Constants.KEY_LAST_DATE, "")
        if (savedDate != today) {
            todaySessions = 0
            prefs.edit().putString(Constants.KEY_LAST_DATE, today).apply()
        } else {
            todaySessions = prefs.getInt(Constants.KEY_SESSIONS_TODAY, 0)
        }
        updateStreakDisplay()
    }

    private fun updateStreakDisplay() {
        binding.streakText.text = "Sessions today: $todaySessions"
    }

    private fun saveStreak() {
        prefs.edit().putInt(Constants.KEY_SESSIONS_TODAY, todaySessions).apply()
    }

    private fun saveTaskName(taskName: String) {
        prefs.edit().putString(Constants.KEY_TASK_NAME, taskName).apply()
    }

    private fun loadTaskName() {
        val taskName = prefs.getString(Constants.KEY_TASK_NAME, "")
        binding.taskNameInput.setText(taskName)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}