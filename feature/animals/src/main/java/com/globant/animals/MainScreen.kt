package com.globant.animals

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.globant.model.features.Animal

private const val BASE_IMAGE_URL =
    "https://raw.githubusercontent.com/CatalinStefan/animalApi/master/"

@Composable
fun MainAnimalRoute(
    viewModel: AnimalViewModel = hiltViewModel(),
    modifier: Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MainAnimalScreen(
        uiState = uiState,
        onRefresh = { viewModel.handleIntent(MainAnimalIntent.Refresh) },
        modifier = modifier
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainAnimalScreen(
    uiState: AnimalUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Transición fluida tipo Crossfade entre estados de la pantalla
    AnimatedContent(
        targetState = uiState,
        transitionSpec = { fadeIn() with fadeOut() },
        label = "ScreenStateTransition",
        modifier = modifier.fillMaxSize()
    ) { state ->
        when (state) {
            is AnimalUiState.Idle -> IdleScreen(onRefresh)
            is AnimalUiState.Loading -> LoadingScreen()
            is AnimalUiState.Success -> AnimalList(animals = state.animals)
            is AnimalUiState.Error -> {
                IdleScreen(onRefresh)
                ErrorEffect(message = state.message)
            }
        }
    }
}

@Composable
fun IdleScreen(
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onButtonClick,
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Fetch Animals", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            strokeWidth = 3.dp,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun AnimalList(
    animals: List<Animal>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp) // Reemplaza los dividers planos por espaciado orgánico
    ) {
        items(
            items = animals,
            key = { it.name } // Clave única para optimizar recomposiciones en listas dinámicas
        ) { animal ->
            AnimalItem(animal = animal)
        }
    }
}

@Composable
fun AnimalItem(
    animal: Animal,
    modifier: Modifier = Modifier
) {
    // Memorizamos la URL para evitar concatenaciones repetitivas en recomposiciones
    val fullImageUrl = remember(animal.image) { "$BASE_IMAGE_URL${animal.image}" }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reemplazo de rememberImagePainter por AsyncImage (API moderna de Coil)
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(fullImageUrl)
                    .crossfade(true) // Animación fade-in nativa deliciosa al cargar la foto
                    .build(),
                contentDescription = "Image of ${animal.name}",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f) // Usa weight en vez de fillMaxSize para respetar el espacio restante de forma limpia
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = animal.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = animal.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ErrorEffect(message: String) {
    val context = LocalContext.current
    // LaunchedEffect garantiza que el Toast solo se ejecute una vez por cada cambio de mensaje
    LaunchedEffect(message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}