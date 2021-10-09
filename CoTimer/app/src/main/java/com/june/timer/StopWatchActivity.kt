package com.june.timer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.june.timer.databinding.ActivityStopWatchBinding

class StopWatchActivity : AppCompatActivity() {

    lateinit var stopWatchVM: StopWatchViewModel
    lateinit var binding: ActivityStopWatchBinding
    private var run: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_stop_watch)

        stopWatchVM = ViewModelProvider(this).get(StopWatchViewModel::class.java)
        binding.lifecycleOwner = this

        stopWatchVM.time.observe(this, Observer { time ->
            calSecToString(time)
        })

        binding.swRunButton.setOnClickListener { runStopWatch() } // 스탑워치 실행
        binding.swClearButton.setOnClickListener { stopWatchVM.stopWatch(0) } // 초기화

        binding.changeTimerButton.setOnClickListener {
            startActivity(Intent(this, TimerActivity::class.java))
        }

    }

    private fun runStopWatch(_run: Boolean = run) {
        if (_run) {
            run = false
            binding.swRunButton.text = "START"
            binding.swRunButton.setBackgroundColor(ContextCompat.getColor(this, R.color.teal_700))
            stopWatchVM.stopWatch()
        } else {
            run = true
            binding.swRunButton.text = "STOP"
            binding.swRunButton.setBackgroundColor(ContextCompat.getColor(this, R.color.magenta_700))
            stopWatchVM.startStopWatch()
        }
    }

    private fun calSecToString(time: Int) {
        var hour = time / 3600
        var min = (time - (hour * 3600)) / 60
        var sec =  (time - (hour * 3600)) % 60

        binding.stTimeTv.text = "${makeText(hour)} : ${makeText(min)} : ${makeText(sec)}"
    }

    private fun makeText(t: Int): String {
        if (t < 10) return "0${t}" else return "$t"
    }
}