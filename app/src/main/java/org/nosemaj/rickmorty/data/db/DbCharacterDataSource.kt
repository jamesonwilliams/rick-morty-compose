package org.nosemaj.rickmorty.data.db

import android.content.Context
import androidx.room.Room
import javax.inject.Inject
import org.nosemaj.rickmorty.data.DataState

class DbCharacterDataSource @Inject constructor(applicationContext: Context) {
    private val db = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java,
        "RickAndMorty"
    ).build()

    suspend fun loadCharacterById(characterId: Int): DataState<DbCharacter> {
        return try {
            DataState.Content(db.characterDao().loadById(characterId))
        } catch (thr: Throwable) {
            DataState.Error(
                Throwable("Unable to get character $characterId from DB.", thr)
            )
        }
    }

    suspend fun loadPageOfCharacters(page: Int): DataState<List<DbCharacter>> {
        return try {
            val ids = ((page - 1) * 20 + 1..page * 20).toSet().toIntArray()
            val characters = db.characterDao().loadAllByIds(ids).sortedBy { it.id }
            if (characters.isEmpty()) {
                return DataState.Error(
                    Throwable("No characters found for page $page.")
                )
            } else {
                return DataState.Content(characters)
            }
        } catch (thr: Throwable) {
            DataState.Error(
                Throwable("Unable to fetch characters for page $page.", thr)
            )
        }
    }

    suspend fun storeCharacters(vararg dbCharacter: DbCharacter): DataState<List<DbCharacter>> {
        return try {
            db.characterDao().insertAll(*dbCharacter)
            DataState.Content(dbCharacter.toList())
        } catch (thr: Throwable) {
            val ids = dbCharacter.map { it.id }
            DataState.Error(
                Throwable("Unable to insert characters $ids to database.", thr)
            )
        }
    }
}
