package org.nosemaj.rickmorty.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.nosemaj.rickmorty.data.CharacterRepository
import org.nosemaj.rickmorty.data.CharacterRepository.Character
import org.nosemaj.rickmorty.data.DataState.Content
import org.nosemaj.rickmorty.data.DataState.Error
import org.nosemaj.rickmorty.ui.list.UiEvent.BottomReached
import org.nosemaj.rickmorty.ui.list.UiEvent.InitialLoad
import org.nosemaj.rickmorty.ui.list.UiEvent.RetryClicked
import javax.inject.Inject

@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val characterRepository: CharacterRepository,
): ViewModel() {
    private val _uiState = MutableStateFlow(UiState.INITIAL)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun onEvent(event: UiEvent) {
        when (event) {
            is RetryClicked -> refreshUi()
            is InitialLoad -> refreshUi()
            is BottomReached -> refreshUi(showLoading = false)
        }
    }

    private fun refreshUi(showLoading: Boolean = true) {
        if (showLoading) {
            _uiState.update { it.copy(displayState = DisplayState.LOADING) }
        }
        viewModelScope.launch {
            val dataState = withContext(Dispatchers.IO) {
                characterRepository.loadCharacters(uiState.value.currentPage)
            }
            when (dataState) {
                is Error<List<Character>> -> {
                    _uiState.update {
                        it.copy(displayState = DisplayState.ERROR, errorMessage = dataState.error.message)
                    }
                }
                is Content<List<Character>> -> {
                    val characterSummaries = dataState.data
                        .map { CharacterSummary(id = it.id, name = it.name, imageUrl = it.image) }
                    _uiState.update {
                        it.copy(
                            characterSummaries = it.characterSummaries.plus(characterSummaries),
                            displayState = DisplayState.CONTENT,
                            currentPage = it.currentPage + 1,
                        )
                    }
                }
            }
        }
    }
}

sealed class UiEvent {
    data object InitialLoad: UiEvent()
    data object RetryClicked: UiEvent()

    data object BottomReached: UiEvent()
}

data class UiState(
    val currentPage: Int,
    val characterSummaries: List<CharacterSummary>,
    val displayState: DisplayState,
    val errorMessage: String?,
) {
    companion object {
        val INITIAL = UiState(
            currentPage = 1,
            characterSummaries = emptyList(),
            displayState = DisplayState.LOADING,
            errorMessage = null,
        )
    }
}

enum class DisplayState {
    LOADING,
    CONTENT,
    ERROR,
    ;
}

data class CharacterSummary(
    val id: Int,
    val name: String,
    val imageUrl: String,
)
