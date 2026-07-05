package com.globant.mvitest.ui.animals

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globant.mvitest.data.api.AnimalRepo
import com.globant.mvitest.domain.GetAnimalsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainAnimalViewModel @Inject constructor(
    private val getAnimalsUseCase: GetAnimalsUseCase
) : ViewModel() {

    val userIntent = Channel<MainAnimalIntent>(Channel.UNLIMITED)
    var uiState = mutableStateOf<MainAnimalState>(MainAnimalState.Idle)
        private set

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect { collector ->
                when (collector) {
                    is MainAnimalIntent.FetchAnimals -> fetchAnimals()
                }
            }
        }
    }

    private fun fetchAnimals() {
        viewModelScope.launch {
            uiState.value = MainAnimalState.Loading
            uiState.value = try {
                MainAnimalState.Animals(getAnimalsUseCase())
            } catch (e: Exception) {
                MainAnimalState.Error(e.localizedMessage)
            }
        }
    }
}