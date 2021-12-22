package com.example.fooddataviewer_mvi.fooddetails

import com.example.fooddataviewer_mvi.model.Product

data class FoodDetailsModel(
    val activity: Boolean = false,
    val product: Product? = null,
    val error: Boolean = false
) {
}