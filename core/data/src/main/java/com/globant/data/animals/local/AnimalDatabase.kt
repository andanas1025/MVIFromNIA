package com.globant.data.animals.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.globant.data.animals.local.dao.AnimalDao
import com.globant.data.animals.local.model.AnimalEntity

@Database(entities = [AnimalEntity::class], version = 1, exportSchema = false)
abstract class AnimalDatabase : RoomDatabase() {
    abstract fun animalDao(): AnimalDao
}