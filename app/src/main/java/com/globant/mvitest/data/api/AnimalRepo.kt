package com.globant.mvitest.data.api

import javax.inject.Inject

class AnimalRepo @Inject constructor(
    private val api: AnimalApi
) {
    suspend fun getAnimals() = api.getAnimals()
}