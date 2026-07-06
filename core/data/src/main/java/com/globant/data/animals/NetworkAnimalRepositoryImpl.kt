package com.globant.data.animals

import com.globant.common.network.DispatcherIO
import com.globant.data.animals.local.AnimalLocalDataSource
import com.globant.data.animals.local.model.toDomain
import com.globant.data.animals.local.model.toEntity
import com.globant.data.animals.remote.AnimalRemoteDataSource
import com.globant.data.animals.remote.errors.toDomainError
import com.globant.model.features.Animal
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.collections.map

@ViewModelScoped
class NetworkAnimalRepositoryImpl @Inject constructor(
    private val remoteDataSource: AnimalRemoteDataSource,
    private val localDataSource: AnimalLocalDataSource,
    @DispatcherIO private val ioDispatcher: CoroutineDispatcher
) : AnimalRepository {

    override fun getAnimalsStream(): Flow<List<Animal>> {
        return localDataSource.getAnimals()
            .map { entities ->
                entities.map { it.toDomain() }
            }
            .catch { exception ->
                // 🚀 Catch local database exceptions and throw as Domain Errors
                throw exception.toDomainError()
            }
    }

    override suspend fun refreshAnimals() = withContext(ioDispatcher) {
        val remoteAnimals = remoteDataSource.fetchAnimals()
        val entities = remoteAnimals.map { it.toEntity() }
        localDataSource.saveAnimals(entities)
    }
}