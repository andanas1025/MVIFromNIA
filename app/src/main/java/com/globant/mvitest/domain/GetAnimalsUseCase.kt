package com.globant.mvitest.domain

import com.globant.common.result.Result
import com.globant.common.result.asResult
import com.globant.model.Animal
import com.globant.mvitest.data.animals.AnimalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAnimalsUseCase @Inject constructor(
    private val repository: AnimalRepository
) {
    operator fun invoke(): Flow<Result<List<Animal>>> {
        return repository.getAnimalsStream().asResult()
    }
}