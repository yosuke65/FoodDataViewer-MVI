package com.example.fooddataviewer_mvi.model

import com.example.fooddataviewer_mvi.model.dto.NutrimentsDto
import com.example.fooddataviewer_mvi.model.dto.ProductDto
import io.reactivex.Single
import javax.inject.Inject

class ProductRepository @Inject constructor(private val productService: ProductService){
     fun getProductFromApi(barcode: String): Single<Product>? {
         return productService.getProduct(barcode)
             ?.map { response ->
                 mapProduct(dto = response.product, saved = false)
             }
     }
}

fun mapProduct(dto: ProductDto, saved: Boolean): Product {
    return Product(
        id = dto._id,
        name = dto.product_name,
        brands = dto.brand_owner,
        imageUrl = dto.image_url,
        ingredients = dto.ingredients_text,
        saved = saved,
        nutriments = mapNutriments(dto.nutriments)
    )
}
private fun mapNutriments(dto: NutrimentsDto?): Nutriments {
    if (dto == null) {
        return Nutriments(
            energy = 0,
            salt = 0.0,
            carbohydrates = 0.0,
            fiber = 0.0,
            sugars = 0.0,
            proteins = 0.0,
            fat = 0.0
        )
    }
    return Nutriments(
        energy = dto.energy,
        salt = dto.salt,
        carbohydrates = dto.carbohydrates,
        fiber = dto.fiber,
        sugars = dto.sugars,
        proteins = dto.proteins,
        fat = dto.fat
    )
}
