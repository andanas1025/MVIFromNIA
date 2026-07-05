package com.globant.mvitest

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberImagePainter
import com.globant.mvitest.data.model.Animal
import com.globant.mvitest.ui.theme.MVITestTheme
import com.globant.mvitest.ui.util.isSystemInDarkTheme
import com.globant.mvitest.ui.animals.MainAnimalIntent
import com.globant.mvitest.ui.animals.MainAnimalState
import com.globant.mvitest.ui.animals.MainAnimalViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainAnimalViewModel: MainAnimalViewModel by viewModels()

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

        val onButtonClick: () -> Unit = {
            lifecycleScope.launch {
                mainAnimalViewModel.userIntent.send(MainAnimalIntent.FetchAnimals)
            }
        }

        splashScreen.setKeepOnScreenCondition {
            mainAnimalViewModel.uiState.value is MainAnimalState.Loading
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
                    MainScreen(
                        mainAnimalViewModel,
                        onButtonClick,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(vm: MainAnimalViewModel, onButtonClick: () -> Unit, modifier: Modifier) {
    val state = vm.uiState.value
    when (state) {
        is MainAnimalState.Idle -> IdleScreen(onButtonClick)
        is MainAnimalState.Loading -> LoadingScreen()
        is MainAnimalState.Animals -> AnimalList(state.animals)
        is MainAnimalState.Error -> {
            IdleScreen(onButtonClick)
            ErrorScreen(state.error)
        }
    }
}

@Composable
fun IdleScreen(onButtonClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = onButtonClick) {
            Text(text = "Fetch Animals")
        }
    }
}

@Composable
fun LoadingScreen() {
    Text(text = "Loading")
}

@Composable
fun AnimalList(animals: List<Animal>) {
    LazyColumn {
        items(items = animals) {
            AnimalItem(animal = it)
            HorizontalDivider(
                color = Color.LightGray,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            )
        }
    }
}

@Composable
fun AnimalItem(animal: Animal) {
    val BASE_URL = "https://raw.githubusercontent.com/CatalinStefan/animalApi/master/"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        val url = BASE_URL + animal.image
        val painter = rememberImagePainter(data = url)
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            contentScale = ContentScale.FillHeight
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 4.dp)
        ) {
            Text(text = animal.name, fontWeight = FontWeight.Bold)
            Text(text = animal.location)
        }
    }
}

@Composable
fun ErrorScreen(error: String) {
    Toast.makeText(LocalContext.current, error, Toast.LENGTH_SHORT).show()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MVITestTheme {
    }
}

data class ThemeSettings(
    val darkTheme: Boolean,
    val androidTheme: Boolean,
    val disableDynamicTheming: Boolean,
)