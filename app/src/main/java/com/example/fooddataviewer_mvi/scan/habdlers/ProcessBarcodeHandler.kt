package com.example.fooddataviewer_mvi.scan.habdlers

import android.util.Log
import com.example.fooddataviewer_mvi.model.ProductRepository
import com.example.fooddataviewer_mvi.scan.BarcodeError
import com.example.fooddataviewer_mvi.scan.ProcessBarcode
import com.example.fooddataviewer_mvi.scan.ProductLoaded
import com.example.fooddataviewer_mvi.scan.ScanEvent
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ProcessBarcodeHandler @Inject constructor(private val productRepository: ProductRepository):
    ObservableTransformer<ProcessBarcode, ScanEvent>{
    override fun apply(upstream: Observable<ProcessBarcode>): ObservableSource<ScanEvent> {
        return upstream.switchMap { effect ->
            productRepository.getProductFromApi(effect.barcode)
                ?.map { product -> ProductLoaded(product) as ScanEvent }
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doOnError { error -> Log.e("ProcessBarcode", error.message, error)}
                ?.onErrorReturnItem(BarcodeError)
                ?.toObservable()
        }
    }
}