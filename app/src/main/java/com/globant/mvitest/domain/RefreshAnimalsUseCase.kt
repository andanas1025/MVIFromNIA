package com.globant.mvitest.domain

import com.globant.mvitest.data.animals.AnimalRepository
import javax.inject.Inject

class RefreshAnimalsUseCase @Inject constructor(
    private val repository: AnimalRepository
) {
    suspend operator fun invoke() {
        repository.refreshAnimals()
    }
}