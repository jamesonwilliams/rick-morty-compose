package org.nosemaj.rickmorty.data.db

import android.content.Context
import androidx.room.Room
import javax.inject.Inject
import org.nosemaj.rickmorty.data.CharacterModel

class DbCharacterDataSource @Inject constructor(applicationContext: Context) {
    private val db = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java,
        AppDatabase::class.simpleName
    ).build()

    suspend fun loadCharacterById(characterId: Int): Result<CharacterModel> {
        return try {
            Result.success(db.characterDao().loadById(characterId))
        } catch (thr: Throwable) {
            Result.failure(Throwable("Unable to get character $characterId from DB.", thr))
        }
    }

    suspend fun loadPageOfCharacters(page: Int): Result<List<CharacterModel>> {
        return try {
            val ids = ((page - 1) * 20 + 1..page * 20).toSet().toIntArray()
            val characters = db.characterDao().loadAllByIds(ids).sortedBy { it.id }
            if (characters.isEmpty()) {
                return Result.failure(Throwable("No characters found for page $page."))
            } else {
                return Result.success(characters)
            }
        } catch (thr: Throwable) {
            Result.failure(Throwable("Unable to fetch characters for page $page.", thr))
        }
    }

    suspend fun storeCharacters(vararg dbCharacter: CharacterModel): Result<List<CharacterModel>> {
        return try {
            db.characterDao().insertAll(*dbCharacter)
            Result.success(dbCharacter.toList())
        } catch (thr: Throwable) {
            val ids = dbCharacter.map { it.id }
            Result.failure(Throwable("Unable to insert characters $ids to database.", thr))
        }
    }
}
