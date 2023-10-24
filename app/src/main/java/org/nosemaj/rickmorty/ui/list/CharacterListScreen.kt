package org.nosemaj.rickmorty.ui.list

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign.Companion.Right
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.nosemaj.rickmorty.ui.list.UiEvent.BottomReached
import org.nosemaj.rickmorty.ui.list.UiEvent.InitialLoad
import org.nosemaj.rickmorty.ui.list.UiEvent.RetryClicked
import org.nosemaj.rickmorty.ui.shared.ErrorUi
import org.nosemaj.rickmorty.ui.shared.LoadingUI
import org.nosemaj.rickmorty.ui.shared.RemoteImage

@Composable
fun CharacterListScreen(
    navigateToCharacter: (characterId: Int) -> Unit,
) {
    val viewModel: CharacterListViewModel = viewModel(
        factory = CharacterListViewModel.Factory,
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
            CharacterList(
                characterSummaries = viewState.characterSummaries,
                onBottomReached = {
                    viewModel.onEvent(BottomReached)
                },
                onCharacterClicked = {
                    navigateToCharacter(it.id)
                }
            )
        }
        DisplayState.ERROR -> {
            ErrorUi(viewState.errorMessage) {
                viewModel.onEvent(RetryClicked)
            }
        }
    }
}

@Composable
fun CharacterList(
    characterSummaries: List<CharacterSummary>,
    onBottomReached: () -> Unit,
    onCharacterClicked: (CharacterSummary) -> Unit,
) {
    val columnCount = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> 4
        else -> 2
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(columnCount)
    ) {
        items(characterSummaries) { summary ->
            CharacterItem(characterSummary = summary) {
                onCharacterClicked(summary)
            }
            if (summary == characterSummaries.last()) {
                onBottomReached()
            }
        }
    }
}

@Composable
fun CharacterItem(
    characterSummary: CharacterSummary,
    modifier: Modifier = Modifier,
    onClicked: () -> Unit,
) {
    Box(
        modifier = modifier.clickable { onClicked() }
    ) {
        RemoteImage(
            imageUrl = characterSummary.imageUrl,
            contentDescription = characterSummary.name
        )
        Text(
            text = characterSummary.name,
            textAlign = Right,
            fontSize = 20.sp,
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.66f))
                .fillMaxWidth()
                .align(BottomEnd)
                .padding(8.dp)
        )
    }
}