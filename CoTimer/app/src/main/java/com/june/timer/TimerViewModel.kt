package com.june.timer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class TimerViewModel : ViewModel() {
    private var sec: Int = 10
    private val _timer = MutableLiveData<String>()
    private lateinit var timeTask: Job

    init {
        _timer.value = "00 : 00 : ${sec}"
    }

    val timer: LiveData<String>
        get() = _timer

    fun clearTimer() {
        timeTask?.cancel()
        sec = 10
        _timer.value = "00 : 00 : ${sec}"
    }

    fun startTimer() {
        viewModelScope.launch {
            timeTask = viewModelScope.launch {
                while (sec > 0) {
                    sec--
                    _timer.value = "00 : 00 : 0${sec}"
                    println("${sec}")
                    delay(1000L)
                }
            }
        }
    }

}


