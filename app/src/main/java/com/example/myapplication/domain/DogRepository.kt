package com.example.myapplication.domain

interface DogRepository {
    suspend fun getBreeds(offset: Int = 0, limit: Int = 10): List<Breed>
}
