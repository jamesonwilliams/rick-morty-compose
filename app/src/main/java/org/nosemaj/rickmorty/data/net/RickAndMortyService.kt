package org.nosemaj.rickmorty.data.net

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface RickAndMortyService {
    @GET("character")
    suspend fun listCharacters(@Query("page") page: Int): Response<CharacterListResponse>

    companion object {
        fun create(baseUrl: String = "https://rickandmortyapi.com/api/"): RickAndMortyService {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(RickAndMortyService::class.java)
        }
    }
}
