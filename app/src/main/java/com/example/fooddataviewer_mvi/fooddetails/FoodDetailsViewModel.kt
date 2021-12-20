package com.example.fooddataviewer_mvi.fooddetails

import com.example.fooddataviewer_mvi.BaseViewModel
import com.example.fooddataviewer_mvi.model.ProductRepository
import com.example.fooddataviewer_mvi.scan.ProductLoaded
import com.spotify.mobius.Next
import com.spotify.mobius.Next.next
import com.spotify.mobius.Update
import com.spotify.mobius.rx2.RxMobius
import javax.inject.Inject

fun foodDetailsUpdate(
    model: FoodDetailsModel,
    event: FoodDetailsEvent
): Next<FoodDetailsModel, FoodDetailsEffect> {
    return when (event) {
        else -> next(model.copy(activity = false))
    }
}

class FoodDetailsViewModel @Inject constructor(
    productRepository: ProductRepository
) : BaseViewModel<FoodDetailsModel, FoodDetailsEvent, FoodDetailsEffect>(
    "FoodDetailsViewModel",
    Update(::foodDetailsUpdate),
    FoodDetailsModel(),
    RxMobius.subtypeEffectHandler<FoodDetailsEffect, FoodDetailsEvent>()
        .build()
)