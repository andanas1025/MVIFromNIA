package com.globant.mvitest.data.api

import com.globant.mvitest.data.model.Animal
import retrofit2.http.GET

interface AnimalApi {

    @GET("animals.json")
    suspend fun getAnimals(): List<Animal>
}