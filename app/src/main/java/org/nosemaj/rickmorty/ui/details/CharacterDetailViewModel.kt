package org.nosemaj.rickmorty.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.nosemaj.rickmorty.data.CharacterListResponse
import org.nosemaj.rickmorty.data.RickAndMortyDataSource
import org.nosemaj.rickmorty.data.RickAndMortyDataSource.DataState
import org.nosemaj.rickmorty.data.RickAndMortyService
import org.nosemaj.rickmorty.ui.details.UiEvent.InitialLoad
import org.nosemaj.rickmorty.ui.details.UiState.Loading

class CharacterDetailViewModel(
    private val dataSource: RickAndMortyDataSource,
): ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun onEvent(uiEvent: UiEvent) {
        when (uiEvent) {
            is InitialLoad -> loadCharacter(uiEvent.characterId)
            is UiEvent.RetryClicked -> loadCharacter(uiEvent.characterId)
        }
    }

    private fun loadCharacter(characterId: Int) {
        viewModelScope.launch {
            val dataState = withContext(Dispatchers.IO) {
                dataSource.getCharacter(characterId = characterId)
            }
            when (dataState) {
                is DataState.Content<CharacterListResponse.Character> -> {
                    val character = dataState.data
                    _uiState.update {
                        UiState.Content(
                            CharacterDetail(
                                id = character.id,
                                name = character.name,
                                imageUrl = character.image,
                                status = character.status,
                                species = character.species,
                                gender = character.gender,
                            )
                        )
                    }
                }
                is DataState.Error<CharacterListResponse.Character> -> {
                    _uiState.update { UiState.Error(dataState.reason) }
                }
            }
        }
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CharacterDetailViewModel::class.java)) {
                val dataSource = RickAndMortyDataSource(RickAndMortyService.create())
                @Suppress("UNCHECKED_CAST")
                return CharacterDetailViewModel(dataSource) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

sealed class UiEvent {
    data class InitialLoad(val characterId: Int): UiEvent()

    data class RetryClicked(val characterId: Int): UiEvent()
}

sealed class UiState {
    object Loading: UiState()

    data class Content(val characterDetail: CharacterDetail): UiState()

    data class Error(val errorMessage: String): UiState()
}

data class CharacterDetail(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val status: String,
    val species: String,
    val gender: String,
)