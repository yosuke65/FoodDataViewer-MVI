package com.example.fooddataviewer_mvi.model

import io.reactivex.Single
import javax.inject.Inject

class ProductRepository @Inject constructor(private val productService: ProductService){
     fun getProductFromApi(barcode: String): Single<Product> {
         return productService.getProduct(barcode)
             .map { response -> mapProduct(dto = response.product, saved = false)}
     }
}

fun mapProduct(dto: ProductDto, saved: Boolean): Product {
    return Product(
        id = dto.id,
        name = dto.product_name,
        brands = dto.brands,
        imageUrl = dto.image_url,
        ingridients = dto.ingredients_text_debug,
        saved = saved
    )
}