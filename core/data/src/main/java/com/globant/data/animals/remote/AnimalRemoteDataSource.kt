package com.globant.data.animals.remote

import com.globant.data.animals.AnimalApi
import com.globant.model.features.Animal
import javax.inject.Inject

class AnimalRemoteDataSource @Inject constructor(
    private val api: AnimalApi
) {
    suspend fun fetchAnimals(): List<Animal> = api.getAnimals()
}