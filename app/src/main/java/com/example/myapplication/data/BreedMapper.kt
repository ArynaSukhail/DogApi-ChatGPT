package com.example.myapplication.data

import com.example.myapplication.domain.Breed

fun BreedDto.toDomain(): Breed =
    Breed(
        id = id,
        name = name,
        temperament = temperament ?: "Unknown",
        weight = weight.metric,
        height = height.metric,
        imageUrl = image?.url ?: ""
    )