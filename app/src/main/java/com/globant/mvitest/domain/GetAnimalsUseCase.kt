package com.globant.mvitest.domain

import com.globant.mvitest.common.Result
import com.globant.mvitest.common.asResult
import com.globant.mvitest.data.animals.AnimalRepository
import com.globant.mvitest.data.model.Animal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAnimalsUseCase @Inject constructor(
    private val repository: AnimalRepository
) {
    operator fun invoke(): Flow<Result<List<Animal>>> {
        return repository.getAnimalsStream().asResult()
    }
}