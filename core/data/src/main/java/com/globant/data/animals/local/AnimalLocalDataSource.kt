package com.globant.data.animals.local

import com.globant.data.animals.local.dao.AnimalDao
import com.globant.data.animals.local.model.AnimalEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AnimalLocalDataSource @Inject constructor(
    private val animalDao: AnimalDao
) {
    fun getAnimals(): Flow<List<AnimalEntity>> = animalDao.getAnimals()

    suspend fun saveAnimals(animals: List<AnimalEntity>) {
        animalDao.insertAnimals(animals)
    }
}