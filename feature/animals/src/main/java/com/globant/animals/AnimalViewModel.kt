package com.globant.animals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globant.animals.AnimalUiState.Loading
import com.globant.animals.MainAnimalIntent.Refresh
import com.globant.common.network.DispatcherDefault
import com.globant.common.result.Result
import com.globant.domain.GetAnimalsUseCase
import com.globant.domain.RefreshAnimalsUseCase
import com.globant.model.errors.DomainError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
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
        syncError.value = throwable
        throwable.printStackTrace()
        _errorState.value = "Sync failed: ${throwable.localizedMessage ?: "An error occurred"}"
    }

    private val syncError = MutableStateFlow<Throwable?>(null)

    val uiState: StateFlow<AnimalUiState> = combine(
        getAnimalsUseCase(),
        syncError
    ) { result, error ->
        when (result) {
            is Result.Loading -> Loading
            is Result.Success -> {
                // If the DB is completely empty AND we just encountered a sync error, show the error!
                if (result.data.isEmpty() || error != null) {
                    AnimalUiState.Error(
                        message = when (error) {
                            is DomainError.NoInternetConnection -> "No Internet Connection available."
                            else -> error?.message ?: "Unknown Error"
                        }
                    )
                } else {
                    // If we have data (cached), show it anyway! (Offline-first beauty)
                    AnimalUiState.Success(result.data)
                }
            }

            is Result.Error -> {
                val message = when (result.exception) {
                    is DomainError.NoInternetConnection -> "Please check your Wi-Fi!"
                    is DomainError.NetworkTimeout -> "The server is taking too long."
                    else -> "Something went wrong."
                }
                AnimalUiState.Error(message)
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

    fun handleIntent(intent: MainAnimalIntent) {
        when (intent) {
            is Refresh -> syncNetworkData()
        }
    }

    private fun syncNetworkData() {
        // 🛡️ Throttling Check: If a sync is already running, drop subsequent clicks
        if (syncJob?.isActive == true) return

        // Launch on Main thread loop (default), letting UseCases handle inner I/O switching
        syncJob = viewModelScope.launch(syncExceptionHandler) {
            _errorState.value = null
            syncError.value = null

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