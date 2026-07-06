package com.globant.mvitest.data.animals

import com.globant.model.Animal
import com.globant.mvitest.data.animals.local.AnimalLocalDataSource
import com.globant.mvitest.data.animals.local.model.toDomain
import com.globant.mvitest.data.animals.local.model.toEntity
import com.globant.mvitest.data.animals.remote.AnimalRemoteDataSource
import com.globant.mvitest.di.DispatcherIO
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
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
        return localDataSource.getAnimals().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun refreshAnimals() = withContext(ioDispatcher) {
        val remoteAnimals = remoteDataSource.fetchAnimals()
        val entities = remoteAnimals.map { it.toEntity() }
        localDataSource.saveAnimals(entities)
    }
}