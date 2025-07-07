package com.example.myapplication.data

import com.example.myapplication.domain.Breed

fun BreedEntity.toDomain() = Breed(
    id = id,
    name = name,
    temperament = temperament,
    weight = weight,
    height = height,
    imageUrl = imageUrl
)

fun Breed.toEntity() = BreedEntity(
    id = id,
    name = name,
    temperament = temperament,
    weight = weight,
    height = height,
    imageUrl = imageUrl
)