package com.example.fooddataviewer_mvi.scan

import com.example.fooddataviewer_mvi.model.Product


data class ScanModel(
    val activity: Boolean = false,
    val processBarcodeResult: ProcessBarcodeResult = ProcessBarcodeResult.Empty
)

sealed class ProcessBarcodeResult {
    object Empty: ProcessBarcodeResult()
    object Error: ProcessBarcodeResult()
    data class ProductLoaded(val product: Product): ProcessBarcodeResult()
}