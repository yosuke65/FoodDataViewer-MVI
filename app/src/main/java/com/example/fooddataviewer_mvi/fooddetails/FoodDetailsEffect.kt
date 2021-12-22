package com.example.fooddataviewer_mvi.fooddetails

import com.example.fooddataviewer_mvi.model.Product

sealed class FoodDetailsEffect

data class LoadProduct(val barcode: String): FoodDetailsEffect()

data class DeleteProduct(val barcode: String): FoodDetailsEffect()

data class SaveProduct(val product: Product): FoodDetailsEffect()