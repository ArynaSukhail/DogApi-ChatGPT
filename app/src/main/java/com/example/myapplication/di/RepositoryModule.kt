package com.example.myapplication.di

import com.example.myapplication.data.DogRepositoryImpl
import com.example.myapplication.data.NetworkChecker
import com.example.myapplication.data.NetworkCheckerImpl
import com.example.myapplication.domain.DogRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindDogRepository(
        impl: DogRepositoryImpl
    ): DogRepository

    @Binds
    abstract fun bindNetworkChecker(
        impl: NetworkCheckerImpl
    ): NetworkChecker
}
