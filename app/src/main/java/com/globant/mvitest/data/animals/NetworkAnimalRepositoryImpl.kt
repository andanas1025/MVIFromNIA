package com.globant.mvitest.data.animals

import com.globant.mvitest.data.animals.local.AnimalLocalDataSource
import com.globant.mvitest.data.animals.local.toDomain
import com.globant.mvitest.data.animals.local.toEntity
import com.globant.mvitest.data.animals.remote.AnimalRemoteDataSource
import com.globant.mvitest.data.model.Animal
import com.globant.mvitest.di.DispatcherIO
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

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

    override suspend fun refreshAnimals() {
        val remoteAnimals = remoteDataSource.fetchAnimals()
        val entities = remoteAnimals.map { it.toEntity() }
        localDataSource.saveAnimals(entities)
    }
}