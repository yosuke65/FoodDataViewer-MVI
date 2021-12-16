package com.example.fooddataviewer_mvi.foodlist

import com.example.fooddataviewer_mvi.BaseViewModel
import com.example.fooddataviewer_mvi.utils.Navigator
import com.spotify.mobius.Next
import com.spotify.mobius.Update
import com.spotify.mobius.rx2.RxMobius
import javax.inject.Inject


fun foodListUpdate(
    model: FoodListModel,
    event: FoodListEvent
): Next<FoodListModel, FoodListEffect> {
    return when(event) {
        FoodListEvent.AddButtonClicked -> Next.dispatch(setOf(FoodListEffect.NavigateToScanner))
    }
}

class FoodListViewModel @Inject constructor(
    navigator: Navigator
): BaseViewModel<FoodListModel, FoodListEvent, FoodListEffect>(
    "FoodListViewModel",
    Update(::foodListUpdate),
    FoodListModel,
    RxMobius.subtypeEffectHandler<FoodListEffect, FoodListEvent>()
        .addAction(FoodListEffect.NavigateToScanner::class.java) {
            navigator.to(FoodListFragmentDirections.scan())
        }
        .build()
){
}