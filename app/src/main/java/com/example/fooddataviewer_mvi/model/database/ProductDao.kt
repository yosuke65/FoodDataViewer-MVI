package com.example.fooddataviewer_mvi.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.fooddataviewer_mvi.model.dto.ProductDto
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
abstract class ProductDao {

    @Query("SELECT * FROM ProductDto")
    abstract fun get(): Observable<List<ProductDto>>

    @Query("SELECT * FROM ProductDto WHERE _id=:barcode")
    abstract fun getProduct(barcode: String): Single<ProductDto>

    @Insert
    abstract fun insert(productDto: ProductDto): Completable

    @Query("DELETE FROM productDto WHERE _id=:barcode")
    abstract fun delete(barcode: String): Completable
}