package com.globant.mvitest.data.animals

import com.globant.model.Animal
import kotlinx.coroutines.flow.Flow

interface AnimalRepository {
    fun getAnimalsStream(): Flow<List<Animal>>
    suspend fun refreshAnimals()
}