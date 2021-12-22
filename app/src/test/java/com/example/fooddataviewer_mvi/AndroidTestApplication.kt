package com.example.fooddataviewer_mvi

import android.app.Application


class AndroidTestApplication : Application() {

    override val component by lazy {
        DaggerTestComponent.builder()
            .context(this)
            .build()
    }
}
