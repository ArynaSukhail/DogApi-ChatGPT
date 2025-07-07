package com.example.myapplication.data

import retrofit2.http.GET

interface DogApi {
    @GET("v1/breeds")
    suspend fun getBreeds(): List<BreedDto>
}