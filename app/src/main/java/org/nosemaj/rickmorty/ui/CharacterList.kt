package org.nosemaj.rickmorty.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import org.nosemaj.rickmorty.R
import org.nosemaj.rickmorty.ui.UiEvent.BottomReached
import org.nosemaj.rickmorty.ui.UiEvent.InitialLoad
import org.nosemaj.rickmorty.ui.UiEvent.RetryClicked

@Composable
fun CharacterScreen() {
    val viewModel: CharacterListViewModel = viewModel(
        factory = CharacterListViewModel.Factory()
    )
    LaunchedEffect(key1 = true) {
        viewModel.onEvent(InitialLoad)
    }
    val viewState by viewModel.uiState.collectAsState()
    when (viewState.displayState) {
        DisplayState.LOADING -> {
            LoadingUI()
        }
        DisplayState.CONTENT -> {
            CharacterList(characters = viewState.characters) {
                viewModel.onEvent(BottomReached)
            }
        }
        DisplayState.ERROR -> {
            ErrorUi(viewState.errorMessage) {
                viewModel.onEvent(RetryClicked)
            }
        }
    }
}

@Composable
fun LoadingUI() {
    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Loading...", fontSize = 20.sp)
    }
}

@Composable
fun CharacterList(
    characters: List<Character>,
    onBottomReached: () -> Unit,
) {
    val columnCount = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> 4
        else -> 2
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(columnCount),
    ) {
        items(characters) { character ->
            CharacterItem(character)
            if (character == characters.last()) {
                onBottomReached()
            }
        }
    }
}

@Composable
fun CharacterItem(
    character: Character,
) {
    Column {
        CharacterImage(imageUrl = character.imageUrl)
        Text(character.name)
    }
}

@Composable
fun CharacterImage(imageUrl: String) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(data = imageUrl)
            .apply(block = fun ImageRequest.Builder.() {
                placeholder(R.drawable.placeholder_drawable)
                error(R.drawable.error_drawable)
            }).build()
        )

    Image(
        painter = painter,
        contentDescription = null, // TODO: Provide a meaningful content description
        modifier = Modifier
            .size(200.dp, 200.dp) // Set the desired size
            .background(Color.Gray) // Optional background color
            .fillMaxWidth(),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun ErrorUi(
    message: String?,
    onRetryClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val errorMessage = if (message != null) "Error: $message" else "Error"
        Text(errorMessage, fontSize = 20.sp)
        Spacer(modifier = Modifier.padding(10.dp))
        Button(
            onClick = onRetryClicked,
        ) {
            Text("Retry?")
        }
    }
}
