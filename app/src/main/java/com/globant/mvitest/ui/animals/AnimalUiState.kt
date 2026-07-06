package com.globant.mvitest.ui.animals

import com.globant.model.Animal


sealed interface AnimalUiState {
    data object Idle : AnimalUiState
    data object Loading : AnimalUiState
    data class Success(val animals: List<Animal>) : AnimalUiState
    data class Error(val error: String) : AnimalUiState

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