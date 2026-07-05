package com.globant.mvitest.data.animals.remote

import com.globant.mvitest.data.animals.AnimalApi
import com.globant.mvitest.data.model.Animal
import javax.inject.Inject

class AnimalRemoteDataSource @Inject constructor(
    private val api: AnimalApi
) {
    suspend fun fetchAnimals(): List<Animal> = api.getAnimals()
}