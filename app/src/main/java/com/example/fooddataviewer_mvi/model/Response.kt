package com.example.fooddataviewer_mvi.model

import com.example.fooddataviewer_mvi.model.dto.ProductDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Response(
    val product: ProductDto
)