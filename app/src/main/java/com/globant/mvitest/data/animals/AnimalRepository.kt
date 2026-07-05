package com.globant.mvitest.data.animals

import com.globant.mvitest.data.model.Animal

interface AnimalRepository {
    suspend fun getAnimals(): List<Animal>
}