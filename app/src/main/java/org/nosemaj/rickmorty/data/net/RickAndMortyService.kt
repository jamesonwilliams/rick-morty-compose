package org.nosemaj.rickmorty.data.net

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface RickAndMortyService {
    @GET("character")
    suspend fun listCharacters(@Query("page") page: Int): Response<CharacterListResponse>

    companion object {
        fun create(baseUrl: String = "https://rickandmortyapi.com/api/"): RickAndMortyService {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(RickAndMortyService::class.java)
        }
    }
}
