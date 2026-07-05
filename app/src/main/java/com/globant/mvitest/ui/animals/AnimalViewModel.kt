package com.globant.mvitest.ui.animals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globant.mvitest.domain.GetAnimalsUseCase
import com.globant.mvitest.ui.animals.MainAnimalIntent.FetchAnimals
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimalViewModel @Inject constructor(
    private val getAnimalsUseCase: GetAnimalsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<AnimalUiState>(AnimalUiState.Idle)
    val uiState: StateFlow<AnimalUiState> = _uiState.asStateFlow()

    fun onIntent(intent: MainAnimalIntent) {
        when (intent) {
            is FetchAnimals -> fetchAnimals()
        }
    }

    private fun fetchAnimals() {
        viewModelScope.launch {
            _uiState.value = AnimalUiState.Loading
            _uiState.value = try {
                AnimalUiState.Success(getAnimalsUseCase())
            } catch (e: Exception) {
                AnimalUiState.Error(e.localizedMessage ?: "An unknown error occurred")
            }
        }
    }
}