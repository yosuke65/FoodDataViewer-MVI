package com.example.fooddataviewer_mvi.utils

import com.example.fooddataviewer_mvi.Activity

class ActivityService {
    private var _activity:Activity? = null
    val activity: Activity
        get() = _activity!!

    fun onCreate(activity: Activity) {
        this._activity = activity
    }

    fun onDestroy(activity: Activity) {
        if(this._activity == activity){
            this._activity = null
        }
    }
}