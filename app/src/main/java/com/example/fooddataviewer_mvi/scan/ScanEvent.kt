package com.example.fooddataviewer_mvi.scan

import com.example.fooddataviewer_mvi.model.Product
import io.fotoapparat.preview.Frame

sealed class ScanEvent

data class  Captured(val frame: Frame): ScanEvent()

data class Detected(val barcode: String): ScanEvent()

data class ProductLoaded(val product: Product): ScanEvent()

object BarcodeError: ScanEvent()