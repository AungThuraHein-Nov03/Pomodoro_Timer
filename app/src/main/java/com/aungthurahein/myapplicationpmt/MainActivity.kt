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
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var timerText: TextView
    private lateinit var circularProgressBar: ProgressBar
    private lateinit var linearProgressBar: ProgressBar
    private lateinit var streakText: TextView
    private lateinit var taskNameInput: EditText
    private lateinit var workTimeInput: EditText
    private lateinit var breakTimeInput: EditText
    private lateinit var statusText: TextView
    private lateinit var startButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var resetButton: ImageButton
    private lateinit var prefs: SharedPreferences

    private var timer: CountDownTimer? = null
    private var isRunning = false
    private var isWorkSession = true
    private var workSeconds = 25 * 60L
    private var shortBreakSeconds = 5 * 60L
    private var longBreakSeconds = 15 * 60L
    private var breakSeconds = 5 * 60L
    private var workCount = 0L
    private var todaySessions = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timerText = findViewById(R.id.timerText)
        circularProgressBar = findViewById(R.id.progressBar)
        linearProgressBar = findViewById(R.id.linearProgressBar)
        streakText = findViewById(R.id.streakText)
        taskNameInput = findViewById(R.id.taskNameInput)
        workTimeInput = findViewById(R.id.workTimeInput)
        breakTimeInput = findViewById(R.id.breakTimeInput)
        statusText = findViewById(R.id.statusText)
        startButton = findViewById(R.id.startButton)
        pauseButton = findViewById(R.id.pauseButton)
        resetButton = findViewById(R.id.resetButton)

        prefs = getSharedPreferences("pomodoro_prefs", Context.MODE_PRIVATE)
        loadStreak()
        loadTaskName()

        startButton.setOnClickListener { startTimer() }
        pauseButton.setOnClickListener { pauseTimer() }
        resetButton.setOnClickListener { resetTimer() }

        taskNameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                saveTaskName(s.toString())
            }
        })

        workTimeInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateWorkTime()
            }
        })

        breakTimeInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateBreakTime()
            }
        })

        workTimeInput.setOnEditorActionListener { _, _, _ ->
            updateWorkTime()
            false
        }
        breakTimeInput.setOnEditorActionListener { _, _, _ ->
            updateBreakTime()
            false
        }

        updateDisplay()
    }

    override fun onResume() {
        super.onResume()
        // Force reset to work mode if returning from break (and not running)
        if (!isRunning && !isWorkSession) {
            isWorkSession = true
            updateDisplay()
            statusText.text = "Ready for work!"
            startButton.visibility = View.VISIBLE
            pauseButton.visibility = View.GONE
            Toast.makeText(this, "Back to work mode!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTimer() {
        if (isRunning) return
        val totalSeconds = if (isWorkSession) workSeconds else breakSeconds
        circularProgressBar.max = totalSeconds.toInt()
        linearProgressBar.max = totalSeconds.toInt()
        circularProgressBar.progress = 0
        linearProgressBar.progress = 0
        timer = object : CountDownTimer(totalSeconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                updateTimer(secondsLeft)
                val progress = (totalSeconds - secondsLeft).toInt()
                circularProgressBar.progress = progress
                linearProgressBar.progress = progress
            }

            override fun onFinish() {
                isRunning = false
                startButton.visibility = View.VISIBLE
                pauseButton.visibility = View.GONE
                timerText.text = "00:00"
                circularProgressBar.progress = circularProgressBar.max
                linearProgressBar.progress = linearProgressBar.max

                if (isWorkSession) {
                    workCount++
                    todaySessions++
                    updateStreakDisplay()
                    saveStreak()
                }

                isWorkSession = !isWorkSession

                if (!isWorkSession) {
                    breakSeconds = if (workCount % 4 == 0L) longBreakSeconds else shortBreakSeconds
                }

                val sessionType = if (isWorkSession) "Work" else "Break"
                val isLongBreak = !isWorkSession && (workCount % 4 == 0L)
                statusText.text = "${if (isLongBreak) "Long " else ""}$sessionType time! Take a ${if (isWorkSession) "break" else "victory lap"}."
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
        startButton.visibility = View.GONE
        pauseButton.visibility = View.VISIBLE
        statusText.text = if (isWorkSession) "Crush that work!" else "Chill out."
    }

    private fun pauseTimer() {
        timer?.cancel()
        isRunning = false
        startButton.visibility = View.VISIBLE
        pauseButton.visibility = View.GONE
        statusText.text = if (isWorkSession) "Work paused. Back at it?" else "Break paused. You do you."
    }

    private fun resetTimer() {
        timer?.cancel()
        isRunning = false
        isWorkSession = true
        workCount = 0L
        startButton.visibility = View.VISIBLE
        pauseButton.visibility = View.GONE
        updateDisplay()
        statusText.text = "Ready for work!"
    }

    private fun updateTimer(seconds: Long) {
        val mins = seconds / 60
        val secs = seconds % 60
        timerText.text = String.format("%02d:%02d", mins, secs)
    }

    private fun updateDisplay() {
        updateTimer(workSeconds)
        circularProgressBar.max = workSeconds.toInt()
        linearProgressBar.max = workSeconds.toInt()
        circularProgressBar.progress = 0
        linearProgressBar.progress = 0
    }

    private fun updateWorkTime() {
        val newMins = workTimeInput.text.toString().toLongOrNull() ?: 25L
        workSeconds = newMins * 60L
        if (!isRunning) updateDisplay()
    }

    private fun updateBreakTime() {
        val newMins = breakTimeInput.text.toString().toLongOrNull() ?: 5L
        shortBreakSeconds = newMins * 60L
        if (workCount % 4 != 0L) {
            breakSeconds = shortBreakSeconds
        }
    }

    private fun loadStreak() {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        val savedDate = prefs.getString("last_date", "")
        if (savedDate != today) {
            todaySessions = 0
            prefs.edit().putString("last_date", today).apply()
        } else {
            todaySessions = prefs.getInt("sessions_today", 0)
        }
        updateStreakDisplay()
    }

    private fun updateStreakDisplay() {
        streakText.text = "Sessions today: $todaySessions"
    }

    private fun saveStreak() {
        prefs.edit().putInt("sessions_today", todaySessions).apply()
    }

    private fun saveTaskName(taskName: String) {
        prefs.edit().putString("task_name", taskName).apply()
    }

    private fun loadTaskName() {
        val taskName = prefs.getString("task_name", "")
        taskNameInput.setText(taskName)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}