package org.nosemaj.rickmorty.data

class RickAndMortyDataSource(
    private val service: RickAndMortyService,
) {
    suspend fun listCharacters(page: Int): DataState {
        return try {
            val response = service.listCharacters(page)
            if (response.isSuccessful) {
                response.body()?.results?.let { characters ->
                    DataState.Content(characters)
                } ?: DataState.Error("No more characters found.")
            } else {
                DataState.Error("Error retrieving characters.")
            }
        } catch (thr: Throwable) {
            val message = thr.message ?: thr.cause?.message ?: "Bad network connectivity."
            DataState.Error(message)
        }
    }

    sealed class DataState {
        data class Content(val characters: List<CharacterResponse.Character>): DataState()

        data class Error(val reason: String): DataState()
    }
}
