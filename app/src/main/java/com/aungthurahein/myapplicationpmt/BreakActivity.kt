package com.aungthurahein.myapplicationpmt

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BreakActivity : AppCompatActivity() {

    private lateinit var breakTimerText: TextView
    private var breakTimer: CountDownTimer? = null
    private var breakSeconds: Long = 5 * 60L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_break)

        val breakMessage: TextView = findViewById(R.id.breakMessage)
        breakTimerText = findViewById(R.id.breakTimerText)

        breakSeconds = intent.getLongExtra("break_seconds", 5 * 60L)

        val backToWorkButton: Button = findViewById(R.id.backToWorkButton)
        backToWorkButton.setOnClickListener {
            finish()  // Manual return triggers Main's onResume reset
        }

        startBreakTimer()
    }

    private fun startBreakTimer() {
        breakTimerText.visibility = View.VISIBLE
        breakTimer = object : CountDownTimer(breakSeconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                val mins = secondsLeft / 60
                val secs = secondsLeft % 60
                breakTimerText.text = String.format("%02d:%02d", mins, secs)
            }

            override fun onFinish() {
                breakTimerText.text = "00:00"
                finish()  // Auto-return to Main, which resets to work via onResume
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        breakTimer?.cancel()
    }
}