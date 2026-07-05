package com.globant.mvitest.ui.animals

sealed class MainAnimalIntent {
    object FetchAnimals: MainAnimalIntent()
}