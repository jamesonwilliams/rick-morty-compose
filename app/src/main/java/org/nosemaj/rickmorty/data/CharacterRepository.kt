package org.nosemaj.rickmorty.data

import javax.inject.Inject
import org.nosemaj.rickmorty.data.DataState.Content
import org.nosemaj.rickmorty.data.DataState.Error
import org.nosemaj.rickmorty.data.db.DbCharacter
import org.nosemaj.rickmorty.data.db.DbCharacterDataSource
import org.nosemaj.rickmorty.data.net.CharacterListResponse
import org.nosemaj.rickmorty.data.net.NetworkCharacterDataSource

class CharacterRepository @Inject constructor(
    private val dbCharacterDataSource: DbCharacterDataSource,
    private val networkCharacterDataSource: NetworkCharacterDataSource
) {
    suspend fun loadCharacters(page: Int): DataState<List<Character>> {
        when (val dbResult = dbCharacterDataSource.loadPageOfCharacters(page = page)) {
            is Error<List<DbCharacter>> -> {}
            is Content<List<DbCharacter>> -> return Content(dbResult.data.map { it.asCharacter() })
        }
        return when (val networkResult = networkCharacterDataSource.listCharacters(page = page)) {
            is Content<CharacterListResponse> -> return save(networkResult.data.results)
            is Error<CharacterListResponse> -> Error(networkResult.error)
        }
    }

    suspend fun getCharacter(characterId: Int): DataState<Character> {
        return when (
            val dbResult = dbCharacterDataSource.loadCharacterById(
                characterId = characterId
            )
        ) {
            is Error<DbCharacter> -> Error(dbResult.error)
            is Content<DbCharacter> -> return Content(dbResult.data.asCharacter())
        }
    }

    private suspend fun save(
        networkCharacters: List<CharacterListResponse.Result>
    ): DataState<List<Character>> {
        val characters = networkCharacters.map { it.asCharacter() }
        val dbCharacters = characters.map { it.asDbCharacter() }.toTypedArray()
        return when (val result = dbCharacterDataSource.storeCharacters(*dbCharacters)) {
            is Content<List<DbCharacter>> -> Content(characters)
            is Error<List<DbCharacter>> -> Error(result.error)
        }
    }

    private fun CharacterListResponse.Result.asCharacter(): Character {
        return Character(
            id = id,
            name = name,
            status = status,
            species = species,
            gender = gender,
            image = image
        )
    }

    private fun DbCharacter.asCharacter(): Character {
        return Character(
            id = id,
            name = name,
            status = status,
            species = species,
            gender = gender,
            image = image
        )
    }

    private fun Character.asDbCharacter(): DbCharacter {
        return DbCharacter(
            id = id,
            name = name,
            status = status,
            species = species,
            gender = gender,
            image = image
        )
    }

    data class Character(
        val id: Int,
        val name: String,
        val status: String,
        val species: String,
        val gender: String,
        val image: String
    )
}
