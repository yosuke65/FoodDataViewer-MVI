package com.example.fooddataviewer_mvi.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fooddataviewer_mvi.model.dto.ProductDto

@Database(
    entities = [ProductDto::class],
    version = 1,
    exportSchema = false
)
abstract class ApplicationDatabase: RoomDatabase() {

    abstract fun productDao(): ProductDao
}