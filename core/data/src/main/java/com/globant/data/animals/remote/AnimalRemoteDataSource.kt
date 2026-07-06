package com.globant.data.animals.remote

import com.globant.model.Animal
import com.globant.data.animals.AnimalApi
import javax.inject.Inject

class AnimalRemoteDataSource @Inject constructor(
    private val api: AnimalApi
) {
    suspend fun fetchAnimals(): List<Animal> = api.getAnimals()
}