package com.example.fooddataviewer_mvi

import io.fotoapparat.preview.Frame

class TestFrameProcessorOnSubscribe : FrameProcessorOnSubscribe() {

    var testFrame: Frame? = null

    override fun invoke(frame: Frame) {
        if (testFrame != null) {
            super.invoke(testFrame!!)
        }
    }
}
