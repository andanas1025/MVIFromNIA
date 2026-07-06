package com.globant.animals

sealed interface MainAnimalIntent {
    data object Refresh: MainAnimalIntent
}