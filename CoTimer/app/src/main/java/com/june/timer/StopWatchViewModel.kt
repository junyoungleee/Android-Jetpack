package com.june.timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StopWatchViewModel : ViewModel() {

    private lateinit var timeTask : Job

    private var sec : Int = 0
    private val _time = MutableLiveData<Int>()
    val time : LiveData<Int>
        get() = _time

    init {
        _time.value = sec
    }

    // 스탑워치 시작
    fun startStopWatch() {
        viewModelScope.launch {
            timeTask = viewModelScope.launch {
                while (true) {
                    sec++
                    _time.value = sec
                    println("$sec")
                    delay(1000L)
                }
            }
        }
    }

    // 스탑워치 멈춤(저장된 시간) / 초기화(0)
    fun stopWatch(initSec: Int = sec) {
        timeTask?.cancel()
        sec = initSec
        println("stopWatch : $sec")
        _time.value = sec
    }




}