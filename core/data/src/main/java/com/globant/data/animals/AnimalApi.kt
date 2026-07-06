package com.globant.data.animals

import com.globant.model.Animal
import retrofit2.http.GET

interface AnimalApi {

    @GET("animals.json")
    suspend fun getAnimals(): List<Animal>
}