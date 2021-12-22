package com.example.fooddataviewer_mvi.foodlist

import com.example.fooddataviewer_mvi.BaseViewModel
import com.example.fooddataviewer_mvi.model.ProductRepository
import com.example.fooddataviewer_mvi.utils.Navigator
import com.spotify.mobius.Next
import com.spotify.mobius.Next.dispatch
import com.spotify.mobius.Next.next
import com.spotify.mobius.Update
import com.spotify.mobius.rx2.RxMobius
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


fun foodListUpdate(
    model: FoodListModel,
    event: FoodListEvent
): Next<FoodListModel, FoodListEffect> {
    return when(event) {
        is Initial -> dispatch(setOf(ObserveProducts))
        is AddButtonClicked -> Next.dispatch(setOf(NavigateToScanner))
        is ProductLoaded -> next(model.copy(products = event.products))
        is ProductClicked -> dispatch(setOf(NavigateToFoodDetails(event.barcode)))
    }
}

class FoodListViewModel @Inject constructor(
    productRepository: ProductRepository,
    navigator: Navigator
): BaseViewModel<FoodListModel, FoodListEvent, FoodListEffect>(
    "FoodListViewModel",
    Update(::foodListUpdate),
    FoodListModel(),
    RxMobius.subtypeEffectHandler<FoodListEffect, FoodListEvent>()
        .addTransformer(ObserveProducts::class.java){ upstream ->
            upstream.switchMap {
                productRepository.get()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { ProductLoaded(it) }
            }
        }
        .addAction(NavigateToScanner::class.java) {
            navigator.to(FoodListFragmentDirections.scan())
        }
        .addConsumer(NavigateToFoodDetails::class.java){ effect ->
            navigator.to(FoodListFragmentDirections.foodDetails(effect.barcode))
        }
        .build()
){
}