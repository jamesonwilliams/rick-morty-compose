package org.nosemaj.rickmorty.ui.list

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
import org.nosemaj.rickmorty.ui.list.UiEvent.BottomReached
import org.nosemaj.rickmorty.ui.list.UiEvent.InitialLoad
import org.nosemaj.rickmorty.ui.list.UiEvent.RetryClicked

class CharacterListViewModel(
    private val rickAndMortyDataSource: RickAndMortyDataSource,
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
                rickAndMortyDataSource.listCharacters(uiState.value.currentPage)
            }
            when (dataState) {
                is DataState.Error<CharacterListResponse> -> {
                    _uiState.update {
                        it.copy(displayState = DisplayState.ERROR, errorMessage = dataState.reason)
                    }
                }
                is DataState.Content<CharacterListResponse> -> {
                    val characterSummaries = dataState.data.results
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

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CharacterListViewModel::class.java)) {
                val dataSource = RickAndMortyDataSource(RickAndMortyService.create())
                @Suppress("UNCHECKED_CAST")
                return CharacterListViewModel(dataSource) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

sealed class UiEvent {
    object InitialLoad: UiEvent()
    object RetryClicked: UiEvent()

    object BottomReached: UiEvent()
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
