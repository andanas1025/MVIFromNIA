package com.globant.mvitest.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.globant.animals.AnimalUiState
import com.globant.animals.AnimalViewModel
import com.globant.animals.MainAnimalRoute
import com.globant.mvitest.ui.theme.MVITestTheme
import com.globant.mvitest.ui.util.isSystemInDarkTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val animalViewModel: AnimalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var themeSettings by mutableStateOf(
            ThemeSettings(
                darkTheme = resources.configuration.isSystemInDarkTheme,
                androidTheme = false, // Default fallback, or read from mainViewModel
                disableDynamicTheming = false // Default fallback, or read from mainViewModel
            )
        )

        splashScreen.setKeepOnScreenCondition {
            animalViewModel.uiState.value is AnimalUiState.Loading
        }

//        lifecycleScope.launch {
//            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                mainViewModel.uiState.collect { state ->
//                    if (state is MainState.Animals) {
//                        // Assuming your Success state contains user settings configuration
//                        themeSettings = ThemeSettings(
//                            darkTheme = state.userPreferences.shouldUseDarkMode ?: resources.configuration.isSystemInDarkTheme,
//                            androidTheme = state.userPreferences.useAndroidTheme,
//                            disableDynamicTheming = state.userPreferences.disableDynamicTheming
//                        )
//                    }
//                }
//            }
//        }

        setContent {
            MVITestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainAnimalRoute()
                }
            }
        }
    }
}

data class ThemeSettings(
    val darkTheme: Boolean,
    val androidTheme: Boolean,
    val disableDynamicTheming: Boolean,
)