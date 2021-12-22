package com.example.fooddataviewer_mvi.foodlist

sealed class FoodListEffect

object ObserveProducts : FoodListEffect()

object NavigateToScanner : FoodListEffect()

data class NavigateToFoodDetails(val barcode: String) : FoodListEffect()
