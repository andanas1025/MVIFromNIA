package com.globant.mvitest.data.di

import com.globant.mvitest.data.animals.AnimalRepository
import com.globant.mvitest.data.animals.NetworkAnimalRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    @ViewModelScoped
    abstract fun bindAnimalRepository(
        networkAnimalRepository: NetworkAnimalRepositoryImpl
    ): AnimalRepository
}