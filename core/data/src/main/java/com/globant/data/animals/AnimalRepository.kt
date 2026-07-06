package com.globant.data.animals

import com.globant.model.features.Animal
import kotlinx.coroutines.flow.Flow

interface AnimalRepository {
    fun getAnimalsStream(): Flow<List<Animal>>
    suspend fun refreshAnimals()
}