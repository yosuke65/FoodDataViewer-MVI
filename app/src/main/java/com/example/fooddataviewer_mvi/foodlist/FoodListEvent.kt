package com.example.fooddataviewer_mvi.foodlist

import com.example.fooddataviewer_mvi.model.Product

sealed class FoodListEvent

object Initial : FoodListEvent()

data class ProductLoaded(val products: List<Product>) : FoodListEvent()

object AddButtonClicked : FoodListEvent()

data class ProductClicked(val barcode: String) : FoodListEvent()