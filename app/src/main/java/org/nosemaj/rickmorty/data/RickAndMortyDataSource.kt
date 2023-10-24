package org.nosemaj.rickmorty.data

import retrofit2.Response

class RickAndMortyDataSource(
    private val service: RickAndMortyService,
) {

    suspend fun getCharacter(characterId: Int): DataState<CharacterListResponse.Character> {
        return fetch(
            "Couldn't get character $characterId.",
            "Error retrieving character $characterId.",
        ) {
            service.getCharacter(characterId)
        }
    }

    suspend fun listCharacters(page: Int): DataState<CharacterListResponse> {
        return fetch(
            "No more characters found.",
            "Error retrieving characters.",
        ) {
            service.listCharacters(page)
        }
    }

    private suspend fun <T> fetch(
        noDataMessage: String,
        unsuccessfulMessage: String,
        loadData: suspend () -> Response<T>,
    ): DataState<T> {
        return try {
            val response = loadData()
            if (response.isSuccessful) {
                response.body()?.let { data ->
                    DataState.Content(data)
                } ?: DataState.Error(noDataMessage)
            } else {
                DataState.Error(unsuccessfulMessage)
            }
        } catch (thr: Throwable) {
            val message = thr.message ?: thr.cause?.message ?: "Bad network connectivity."
            DataState.Error(message)
        }
    }

    sealed class DataState<T> {
        data class Content<T>(val data: T): DataState<T>()

        data class Error<T>(val reason: String): DataState<T>()
    }
}
