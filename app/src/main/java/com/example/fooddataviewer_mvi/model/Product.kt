package com.example.fooddataviewer_mvi.model

data class Product(
    val id: String,
    val saved: Boolean,
    val name: String,
    val brands: String,
    val imageUrl: String,
    val ingridients: String,
    val nutriments: String? = null
)