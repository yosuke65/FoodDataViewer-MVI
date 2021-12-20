package com.example.fooddataviewer_mvi.model

import com.example.fooddataviewer_mvi.model.dto.NutrimentsDto

data class Product(
    val id: String,
    val saved: Boolean,
    val name: String,
    val brands: String,
    val imageUrl: String,
    val ingredients: String?,
    val nutriments: Nutriments? = null
)