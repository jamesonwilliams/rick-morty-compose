package org.nosemaj.rickmorty.data.net

import javax.inject.Inject
import org.nosemaj.rickmorty.data.CharacterModel
import retrofit2.Response

class NetworkCharacterDataSource @Inject constructor(
    private val service: RickAndMortyService
) {
    suspend fun listCharacters(page: Int): Result<CharacterListResponse> {
        return fetch(
            "No more characters found.",
            "Error retrieving characters."
        ) {
            service.listCharacters(page)
        }
    }

    suspend fun getCharacter(characterId: Int): Result<CharacterModel> {
        return fetch(
            "No character $characterId found",
            "Error retrieving character $characterId"
        ) {
            service.getCharacter(characterId)
        }
    }

    private suspend fun <T> fetch(
        noDataMessage: String,
        unsuccessfulMessage: String,
        loadData: suspend () -> Response<T>
    ): Result<T> {
        return try {
            val response = loadData()
            if (response.isSuccessful) {
                response.body()?.let { data -> Result.success(data) }
                    ?: Result.failure(Throwable(noDataMessage))
            } else {
                Result.failure(Throwable(unsuccessfulMessage))
            }
        } catch (thr: Throwable) {
            Result.failure(Throwable("Bad network connectivity.", thr))
        }
    }
}
