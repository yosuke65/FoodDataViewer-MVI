package com.example.fooddataviewer_mvi.foodlist

sealed class FoodListEvent {
    object AddButtonClicked: FoodListEvent()
}