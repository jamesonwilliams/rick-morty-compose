package org.nosemaj.rickmorty.data

import javax.inject.Inject
import org.nosemaj.rickmorty.data.db.DbCharacterDataSource
import org.nosemaj.rickmorty.data.net.NetworkCharacterDataSource

class CharacterRepository @Inject constructor(
    private val dbCharacterDataSource: DbCharacterDataSource,
    private val networkCharacterDataSource: NetworkCharacterDataSource
) {
    suspend fun loadCharacters(page: Int): Result<List<CharacterModel>> {
        return dbCharacterDataSource.loadPageOfCharacters(page = page)
            .onFailure {
                return networkCharacterDataSource.listCharacters(page = page)
                    .map { it.results }
                    .onSuccess { characters ->
                        dbCharacterDataSource.storeCharacters(*characters.toTypedArray())
                    }
            }
    }

    suspend fun getCharacter(characterId: Int): Result<CharacterModel> {
        return dbCharacterDataSource.loadCharacterById(characterId = characterId)
            .onFailure {
                return networkCharacterDataSource.getCharacter(characterId)
                    .onSuccess {
                        dbCharacterDataSource.storeCharacters(it)
                    }
            }
    }
}
