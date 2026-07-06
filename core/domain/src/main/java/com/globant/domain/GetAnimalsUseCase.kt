package com.globant.domain

import com.globant.common.result.Result
import com.globant.common.result.asResult
import com.globant.data.animals.AnimalRepository
import com.globant.model.features.Animal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAnimalsUseCase @Inject constructor(
    private val repository: AnimalRepository
) {
    operator fun invoke(): Flow<Result<List<Animal>>> {
        return repository.getAnimalsStream().asResult()
    }
}