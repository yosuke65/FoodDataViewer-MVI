package com.example.fooddataviewer_mvi.scan

import com.example.fooddataviewer_mvi.BaseViewModel
import com.example.fooddataviewer_mvi.scan.habdlers.ProcessBarcodeHandler
import com.example.fooddataviewer_mvi.scan.habdlers.ProcessFrameHandler
import com.example.fooddataviewer_mvi.utils.Navigator
import com.spotify.mobius.Next
import com.spotify.mobius.Update
import com.spotify.mobius.rx2.RxMobius
import javax.inject.Inject


fun scanUpdate(
    model: ScanModel,
    event: ScanEvent
): Next<ScanModel, ScanEffect> {
    return when(event) {
        is Captured -> Next.dispatch(setOf(ProcessCameraFrame(event.frame)))
        is Detected -> if(!model.activity) {
            Next.next(
                model.copy(activity = true),
                setOf(ProcessBarcode(event.barcode))
            )
        }
        else {
            Next.noChange()
        }
        is BarcodeError -> Next.next(
            model.copy(
                activity = false, processBarcodeResult = ProcessBarcodeResult.Error
            )
        )
        is ProductLoaded -> Next.next(
            model.copy(
                activity = false,
                processBarcodeResult = ProcessBarcodeResult.BarcodeLoaded(event.product)
            )
        )
        is ProductInfoClicked -> if (model.processBarcodeResult is ProcessBarcodeResult.BarcodeLoaded) {
            Next.dispatch(setOf(NavigateToFoodDetails(model.processBarcodeResult.product.id)))
        } else {
            Next.noChange()
        }
    }
}

class ScanViewModel @Inject constructor(
    processFrameHandler: ProcessFrameHandler,
    processBarcodeHandler: ProcessBarcodeHandler,
    navigator: Navigator
): BaseViewModel<ScanModel, ScanEvent, ScanEffect>(
    "ScanViewModel",
    Update(::scanUpdate),
    ScanModel(),
    RxMobius.subtypeEffectHandler<ScanEffect, ScanEvent>()
        .addTransformer(ProcessCameraFrame::class.java, processFrameHandler)
        .addTransformer(ProcessBarcode::class.java, processBarcodeHandler)
        .addConsumer(NavigateToFoodDetails::class.java) { effect ->
            navigator.to(ScanFragmentDirections.foodDetails(effect.barcode))
        }
        .build()
)