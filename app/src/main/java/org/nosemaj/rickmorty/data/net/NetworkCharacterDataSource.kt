package org.nosemaj.rickmorty.data.net

import android.util.Log
import org.nosemaj.rickmorty.data.DataState
import retrofit2.Response

class NetworkCharacterDataSource(
    private val service: RickAndMortyService,
) {
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
                } ?: DataState.Error(
                    Throwable(noDataMessage)
                )
            } else {
                DataState.Error(
                    Throwable(unsuccessfulMessage)
                )
            }
        } catch (thr: Throwable) {
            DataState.Error(
                Throwable("Bad network connectivity.", thr)
            )
        }
    }
}
