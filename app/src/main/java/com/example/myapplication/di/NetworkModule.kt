package com.example.myapplication.di

import com.example.myapplication.data.DogApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideBaseUrl() = "http://api.thedogapi.com/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(provideBaseUrl())
        .addConverterFactory(GsonConverterFactory.create()) // ← вот здесь
        .build()

    @Provides
    @Singleton
    fun provideDogApi(retrofit: Retrofit): DogApi =
        retrofit.create(DogApi::class.java)
}
