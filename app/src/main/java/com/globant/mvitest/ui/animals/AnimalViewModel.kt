package com.globant.mvitest.ui.animals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globant.mvitest.common.Result
import com.globant.mvitest.data.model.Animal
import com.globant.mvitest.di.DispatcherDefault
import com.globant.mvitest.di.DispatcherIO
import com.globant.mvitest.domain.GetAnimalsUseCase
import com.globant.mvitest.domain.RefreshAnimalsUseCase
import com.globant.mvitest.ui.animals.AnimalUiState.Idle
import com.globant.mvitest.ui.animals.AnimalUiState.Loading
import com.globant.mvitest.ui.animals.AnimalUiState.Success
import com.globant.mvitest.ui.animals.MainAnimalIntent.FetchAnimals
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimalViewModel @Inject constructor(
    private val getAnimalsUseCase: GetAnimalsUseCase,
    private val refreshAnimalsUseCase: RefreshAnimalsUseCase,
    @DispatcherIO private val ioDispatcher: CoroutineDispatcher,
    @DispatcherDefault private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {
    val uiState: StateFlow<AnimalUiState> = getAnimalsUseCase()
        .map { result: Result<List<Animal>> ->
            when (result) {
                is Result.Loading -> Loading
                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        Idle
                    } else {
                        Success(result.data)
                    }
                }

                is Result.Error -> {
                    AnimalUiState.Error(result.exception.localizedMessage ?: "An error occurred")
                }
            }
        }
        .flowOn(defaultDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Loading
        )

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState = _errorState.asStateFlow()

    fun onIntent(intent: MainAnimalIntent) {
        when (intent) {
            is FetchAnimals -> syncNetworkData()
        }
    }

    private fun syncNetworkData() {
        viewModelScope.launch(ioDispatcher) {
            try {
                _errorState.value = null
                refreshAnimalsUseCase()
            } catch (e: Exception) {
                _errorState.value = e.localizedMessage ?: "Failed to sync with server"
            }
        }
    }
}