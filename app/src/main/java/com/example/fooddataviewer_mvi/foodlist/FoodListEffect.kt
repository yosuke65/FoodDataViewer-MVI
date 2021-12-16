package com.example.fooddataviewer_mvi.foodlist

sealed class FoodListEffect {
   object NavigateToScanner: FoodListEffect()
}