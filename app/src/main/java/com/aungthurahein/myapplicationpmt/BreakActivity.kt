package com.aungthurahein.myapplicationpmt

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aungthurahein.myapplicationpmt.databinding.ActivityBreakBinding

class BreakActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBreakBinding
    private var breakTimer: CountDownTimer? = null
    private var breakSeconds: Long = Constants.minutesToSeconds(Constants.DEFAULT_SHORT_BREAK_MINUTES)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBreakBinding.inflate(layoutInflater)
        setContentView(binding.root)

        breakSeconds = intent.getLongExtra("break_seconds", Constants.minutesToSeconds(Constants.DEFAULT_SHORT_BREAK_MINUTES))

        binding.backToWorkButton.setOnClickListener {
            finish()  // Manual return triggers Main's onResume reset
        }

        startBreakTimer()
    }

    private fun startBreakTimer() {
        binding.breakTimerText.visibility = View.VISIBLE
        breakTimer = object : CountDownTimer(breakSeconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                val mins = secondsLeft / 60
                val secs = secondsLeft % 60
                binding.breakTimerText.text = String.format("%02d:%02d", mins, secs)
            }

            override fun onFinish() {
                binding.breakTimerText.text = "00:00"
                finish()  // Auto-return to Main, which resets to work via onResume
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        breakTimer?.cancel()
    }
}