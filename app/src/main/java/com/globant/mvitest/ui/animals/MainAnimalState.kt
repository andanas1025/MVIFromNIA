package com.globant.mvitest.ui.animals

import com.globant.mvitest.data.model.Animal

sealed interface MainAnimalState {
    data object Idle : MainAnimalState
    data object Loading : MainAnimalState
    data class Animals(val animals: List<Animal>) : MainAnimalState
    data class Error(val error: String) : MainAnimalState

    /**
     * Returns `true` if the state wasn't loaded yet and it should keep showing the splash screen.
     */
    fun shouldKeepSplashScreen() = this is Loading

    /**
     * Returns `true` if the dynamic color is disabled.
     */
    val shouldDisableDynamicTheming: Boolean get() = true

    /**
     * Returns `true` if the Android theme should be used.
     */
    val shouldUseAndroidTheme: Boolean get() = false

    /**
     * Returns `true` if dark theme should be used.
     */
    fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) = isSystemDarkTheme
}