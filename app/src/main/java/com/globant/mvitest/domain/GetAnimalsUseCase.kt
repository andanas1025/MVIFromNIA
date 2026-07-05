package com.globant.mvitest.domain

import com.globant.mvitest.data.animals.AnimalRepository
import com.globant.mvitest.data.model.Animal
import javax.inject.Inject

class GetAnimalsUseCase @Inject constructor(
    private val repository: AnimalRepository
) {
    suspend operator fun invoke(): List<Animal> {
        return repository.getAnimals()
    }
}