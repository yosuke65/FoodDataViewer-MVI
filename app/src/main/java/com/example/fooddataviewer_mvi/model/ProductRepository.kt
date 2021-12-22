package com.example.fooddataviewer_mvi.model

import com.example.fooddataviewer_mvi.model.database.ProductDao
import com.example.fooddataviewer_mvi.model.dto.NutrimentsDto
import com.example.fooddataviewer_mvi.model.dto.ProductDto
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val productService: ProductService,
    private val productDao: ProductDao
    ){

    fun get(): Observable<List<Product>> {
        return productDao.get()
            .map { it.map { dto -> mapProduct(dto,true)} }
    }

    fun loadProduct(barcode: String): Single<Product> {
        return getProductFromDatabase(barcode)
            .onErrorResumeNext(getProductFromApi(barcode))
    }

    fun getProductFromDatabase(barcode: String): Single<Product> {
        return productDao.getProduct(barcode)
            .map { product -> mapProduct(product, true) }
    }

     fun getProductFromApi(barcode: String): Single<Product>? {
         return productService.getProduct(barcode)
             ?.map { response ->
                 mapProduct(dto = response.product, saved = false)
             }
     }

    fun saveProduct(product: Product): Completable {
        return Single.fromCallable{ mapProductDto(product)}
            .flatMapCompletable { productDto ->
                productDao.insert(productDto)
            }
    }

    fun deleteProduct(barcode: String): Completable {
        return productDao.delete(barcode)
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

private fun mapProductDto(product: Product): ProductDto {
    return ProductDto(
        _id = product.id,
        product_name = product.name,
        brand_owner = product.brands,
        image_url = product.imageUrl,
        ingredients_text = product.ingredients,
        nutriments = mapNutrimentsDto(product.nutriments)
    )
}

fun mapNutrimentsDto(nutriments: Nutriments?): NutrimentsDto? {
    if(nutriments == null) return null
    return NutrimentsDto(
        energy = nutriments.energy,
        salt = nutriments.salt,
        carbohydrates = nutriments.carbohydrates,
        fiber = nutriments.fiber,
        sugars = nutriments.sugars,
        proteins = nutriments.proteins,
        fat = nutriments.fat
    )
}
