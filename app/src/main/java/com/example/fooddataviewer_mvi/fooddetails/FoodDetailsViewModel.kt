package com.example.fooddataviewer_mvi.fooddetails

import com.example.fooddataviewer_mvi.BaseViewModel
import com.example.fooddataviewer_mvi.model.ProductRepository
import com.example.fooddataviewer_mvi.fooddetails.ProductLoaded
import com.spotify.mobius.Next
import com.spotify.mobius.Next.*
import com.spotify.mobius.Update
import com.spotify.mobius.rx2.RxMobius
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

fun foodDetailsUpdate(
    model: FoodDetailsModel,
    event: FoodDetailsEvent
): Next<FoodDetailsModel, FoodDetailsEffect> {
    return when (event) {
        is Initial -> next(
            model.copy(activity = true), setOf(LoadProduct(event.barcode))
        )

        is ProductLoaded -> next(model.copy(activity = false, product = event.product))
        is ErrorLoadingProduct -> next(model.copy(activity = false, error = true))
        is ActionButtonClicked -> if(model.product != null) {
            if(model.product.saved)
                dispatch(setOf(DeleteProduct(model.product.id)))
            else
                dispatch(setOf(SaveProduct(model.product)))

        } else {
            noChange()
        }
    }
}

class FoodDetailsViewModel @Inject constructor(
    productRepository: ProductRepository
) : BaseViewModel<FoodDetailsModel, FoodDetailsEvent, FoodDetailsEffect>(
    "FoodDetailsViewModel",
    Update(::foodDetailsUpdate),
    FoodDetailsModel(),
    RxMobius.subtypeEffectHandler<FoodDetailsEffect, FoodDetailsEvent>()
        .addTransformer(LoadProduct::class.java){ upstream ->
            upstream.switchMap { effect ->
                productRepository.loadProduct(barcode = effect.barcode)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .toObservable()
                    .map { product -> ProductLoaded(product) as FoodDetailsEvent }
                    .onErrorReturn { ErrorLoadingProduct }
            }
        }
        .addTransformer(SaveProduct::class.java) { upstream ->
            upstream.switchMap { effect ->
                productRepository.saveProduct(effect.product)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .toObservable()
            }
        }
        .addTransformer(DeleteProduct::class.java) { upstream ->
            upstream.switchMap { effect ->
                productRepository.deleteProduct(effect.barcode)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .toObservable()
            }
        }
        .build()
)