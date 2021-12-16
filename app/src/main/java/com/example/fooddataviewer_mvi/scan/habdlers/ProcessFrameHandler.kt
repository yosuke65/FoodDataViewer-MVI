package com.example.fooddataviewer_mvi.scan.habdlers

import com.example.fooddataviewer_mvi.scan.Detected
import com.example.fooddataviewer_mvi.scan.ProcessCameraFrame
import com.example.fooddataviewer_mvi.scan.ScanEvent
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.internal.BarcodeScannerImpl
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.common.InputImage.IMAGE_FORMAT_NV21
import com.google.mlkit.vision.common.internal.VisionImageMetadataParcel
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject

class ProcessFrameHandler @Inject constructor():
    ObservableTransformer<ProcessCameraFrame, ScanEvent> {

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_CODE_128)
            .build()

    override fun apply(upstream: Observable<ProcessCameraFrame>): ObservableSource<ScanEvent> {
        return upstream.flatMap { effect ->
            Observable.create<ScanEvent> { emitter ->
                with(effect.frame) {
                    val image = InputImage.fromByteArray(
                        image,
                        size.width,
                        size.height,
                        rotation,
                        IMAGE_FORMAT_NV21
                    )
                    val detector = BarcodeScanning.getClient(options).process(image)
                    detector
                        .addOnSuccessListener { barcodes ->
                            if (barcodes.isNotEmpty()) {
                                emitter.onNext(Detected(barcodes[0].rawValue.toString()))
                            }
                        }
                        .addOnFailureListener {
                            emitter.onComplete()
                        }
                }
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

}