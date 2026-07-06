package com.globant.data.di

import android.content.Context
import androidx.room.Room
import com.globant.data.animals.local.AnimalDatabase
import com.globant.data.animals.local.dao.AnimalDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AnimalDatabase {
        return Room.databaseBuilder(
            context,
            AnimalDatabase::class.java,
            "animal_db"
        ).build()
    }

    @Provides
    fun provideAnimalDao(database: AnimalDatabase): AnimalDao = database.animalDao()
}