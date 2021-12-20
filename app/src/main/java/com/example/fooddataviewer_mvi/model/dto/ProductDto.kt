package com.example.fooddataviewer_mvi.model.dto

import com.example.fooddataviewer_mvi.model.Nutriments
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductDto(
    val _id: String,
    val product_name: String,
    val brand_owner: String,
    val image_url: String,
    val ingredients_text: String,
    val nutriments: NutrimentsDto?
)