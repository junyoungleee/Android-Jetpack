package com.june.timer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.june.timer.databinding.ActivityTimerBinding

class TimerActivity : AppCompatActivity() {

    lateinit var timerVM: TimerViewModel
    lateinit var binding: ActivityTimerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timer)

        timerVM = ViewModelProvider(this).get(TimerViewModel::class.java)
        binding.viewModel = timerVM

        binding.lifecycleOwner = this
        binding.changeSwButton.setOnClickListener {
            startActivity(Intent(this, StopWatchActivity::class.java))
        }
    }
}