package org.nosemaj.rickmorty.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.nosemaj.rickmorty.ui.details.UiState.Content
import org.nosemaj.rickmorty.ui.details.UiState.Error
import org.nosemaj.rickmorty.ui.details.UiState.Loading
import org.nosemaj.rickmorty.ui.shared.ErrorUi
import org.nosemaj.rickmorty.ui.shared.LoadingUI
import org.nosemaj.rickmorty.ui.shared.RemoteImage

@Composable
fun CharacterDetailScreen(onBackPressed: () -> Unit) {
    val viewModel: CharacterDetailViewModel = hiltViewModel()

    LaunchedEffect(key1 = true) {
        viewModel.onEvent(UiEvent.InitialLoad)
    }
    val viewState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val currentState = viewState) {
        is Loading -> LoadingUI()
        is Content -> CharacterDetailUi(currentState.characterDetail) {
            onBackPressed()
        }
        is Error -> {
            ErrorUi(currentState.errorMessage) {
                viewModel.onEvent(UiEvent.RetryClicked)
            }
        }
    }
}

@Composable
fun CharacterDetailUi(characterDetail: CharacterDetail, onBackClicked: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxHeight()
    ) {
        Box {
            RemoteImage(
                imageUrl = characterDetail.imageUrl,
                contentDescription = characterDetail.name
            )
            BackBar {
                onBackClicked()
            }
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            TitleLine(characterDetail.name)
            DetailLine(characterDetail.gender)
            DetailLine(characterDetail.species)
            DetailLine(characterDetail.status)
        }
    }
}

@Composable
fun TitleLine(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

@Composable
fun DetailLine(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
fun BackBar(onBackClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.66f))
    ) {
        IconButton(onClick = {
            onBackClicked()
        }) {
            Icon(Icons.Filled.ArrowBack, "Back buton")
        }
    }
}
