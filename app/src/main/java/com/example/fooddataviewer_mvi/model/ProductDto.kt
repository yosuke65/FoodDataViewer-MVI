package com.example.fooddataviewer_mvi.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductDto(
    val id: String,
    val product_name: String,
    val brands: String,
    val image_url: String,
    val ingredients_text_debug: String
)