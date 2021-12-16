package com.example.fooddataviewer_mvi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fooddataviewer_mvi.utils.ActivityService

class Activity : AppCompatActivity() {
    private lateinit var activityService: ActivityService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityService = applicationContext.component.activityService()
        activityService.onCreate(this)
        setContentView(R.layout.activity_main)

    }

    override fun onDestroy() {
        activityService.onDestroy(this)
        super.onDestroy()
    }
}