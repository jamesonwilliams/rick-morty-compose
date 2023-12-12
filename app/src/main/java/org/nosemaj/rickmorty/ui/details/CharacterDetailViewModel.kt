package org.nosemaj.rickmorty.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.nosemaj.rickmorty.data.CharacterRepository
import org.nosemaj.rickmorty.ui.details.UiEvent.InitialLoad
import org.nosemaj.rickmorty.ui.details.UiEvent.RetryClicked
import org.nosemaj.rickmorty.ui.details.UiState.Loading

@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val characterRepository: CharacterRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val characterId: Int = checkNotNull(savedStateHandle["characterId"])
    private val _uiState = MutableStateFlow<UiState>(Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun onEvent(uiEvent: UiEvent) {
        when (uiEvent) {
            is InitialLoad -> loadCharacter()
            is RetryClicked -> loadCharacter()
        }
    }

    private fun loadCharacter() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                characterRepository.getCharacter(characterId = characterId)
            }
                .onSuccess { character ->
                    _uiState.update {
                        UiState.Content(
                            CharacterDetail(
                                id = character.id,
                                name = character.name,
                                imageUrl = character.image,
                                status = character.status,
                                species = character.species,
                                gender = character.gender
                            )
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { UiState.Error(error.message) }
                }
        }
    }
}

sealed class UiEvent {
    data object InitialLoad : UiEvent()
    data object RetryClicked : UiEvent()
}

sealed class UiState {
    data object Loading : UiState()
    data class Content(val characterDetail: CharacterDetail) : UiState()
    data class Error(val errorMessage: String?) : UiState()
}

data class CharacterDetail(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val status: String,
    val species: String,
    val gender: String
)
