package com.example.myapplication.data

import java.io.Serializable

data class BreedDto(
    val id: Int,
    val name: String,
    val temperament: String?,
    val weight: Measure,
    val height: Measure,
    val image: Image?
) : Serializable

data class Measure(val metric: String) : java.io.Serializable
data class Image(val url: String?) : java.io.Serializable
