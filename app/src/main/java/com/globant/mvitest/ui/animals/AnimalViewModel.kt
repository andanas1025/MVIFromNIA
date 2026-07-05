package com.globant.mvitest.ui.animals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globant.mvitest.common.Result
import com.globant.mvitest.data.model.Animal
import com.globant.mvitest.di.DispatcherDefault
import com.globant.mvitest.domain.GetAnimalsUseCase
import com.globant.mvitest.domain.RefreshAnimalsUseCase
import com.globant.mvitest.ui.animals.AnimalUiState.Idle
import com.globant.mvitest.ui.animals.AnimalUiState.Loading
import com.globant.mvitest.ui.animals.AnimalUiState.Success
import com.globant.mvitest.ui.animals.MainAnimalIntent.FetchAnimals
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

@HiltViewModel
class AnimalViewModel @Inject constructor(
    getAnimalsUseCase: GetAnimalsUseCase,
    private val refreshAnimalsUseCase: RefreshAnimalsUseCase,
    @DispatcherDefault private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    // Keep track of the active sync job to prevent spamming multiple concurrent requests
    private var syncJob: Job? = null

    // Fallback handler for unhandled background exceptions
    private val syncExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _errorState.value = "Sync failed: ${throwable.localizedMessage ?: "An error occurred"}"
    }
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
        // 🛡️ Throttling Check: If a sync is already running, drop subsequent clicks
        if (syncJob?.isActive == true) return

        // Launch on Main thread loop (default), letting UseCases handle inner I/O switching
        syncJob = viewModelScope.launch(syncExceptionHandler) {
            _errorState.value = null

            // Supervisor boundary insulates concurrent tasks safely
            supervisorScope {
                launch {
                    refreshAnimalsUseCase()
                }

                // Add any other independent flow launch block here if needed later!
            }
        }
    }
}

// Example inside a searching/filtering flow:
//val filteredAnimals = searchQueryStateFlow
//    .debounce(300) // ⏳ Wait 300ms for the user to stop typing before propagating downstream
//    .distinctUntilChanged() // 🚫 If they typed "A", deleted it, and typed "A" again quickly, ignore the duplication
//    .map { query ->
//        getAnimalsUseCase(query)
//    }