package com.globant.mvitest.data.animals

import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class NetworkAnimalRepositoryImpl @Inject constructor(
    private val api: AnimalApi
) : AnimalRepository {
    override suspend fun getAnimals() = api.getAnimals()
}