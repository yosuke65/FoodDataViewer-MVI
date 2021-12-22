package com.example.fooddataviewer_mvi.fooddetails

import com.example.fooddataviewer_mvi.model.Product

sealed class FoodDetailsEvent

data class Initial(val barcode: String): FoodDetailsEvent()

object ActionButtonClicked: FoodDetailsEvent()

data class ProductLoaded(val product: Product): FoodDetailsEvent()

object ErrorLoadingProduct: FoodDetailsEvent()