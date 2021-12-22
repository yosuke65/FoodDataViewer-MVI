package com.example.fooddataviewer_mvi.model.dto

import androidx.room.Entity
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NutrimentsDto(
    val energy: Int?,
    val salt: Double?,
    val carbohydrates: Double?,
    val fiber: Double?,
    val sugars: Double?,
    val proteins: Double?,
    val fat: Double?
)