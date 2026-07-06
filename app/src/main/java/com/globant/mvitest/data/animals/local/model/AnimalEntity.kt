package com.globant.mvitest.data.animals.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.globant.model.Animal

@Entity(tableName = "animals")
data class AnimalEntity(
    @PrimaryKey val name: String,
    val location: String,
    val image: String
)

fun AnimalEntity.toDomain() = Animal(name = name, location = location, image = image)
fun Animal.toEntity() = AnimalEntity(name = name, location = location, image = image)