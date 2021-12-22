package com.example.fooddataviewer_mvi.model.dto

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fooddataviewer_mvi.model.Nutriments
import com.squareup.moshi.JsonClass

@Entity
@JsonClass(generateAdapter = true)
data class ProductDto(
    @PrimaryKey val _id: String,
    val product_name: String,
    val brand_owner: String,
    val image_url: String,
    val ingredients_text: String?,
    @Embedded val nutriments: NutrimentsDto?
)