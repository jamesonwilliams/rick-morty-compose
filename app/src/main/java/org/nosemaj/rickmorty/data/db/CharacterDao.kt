package org.nosemaj.rickmorty.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import org.nosemaj.rickmorty.data.CharacterModel

@Dao
interface CharacterDao {
    @Query("SELECT * FROM charactermodel WHERE id IN (:characterIds) ORDER BY id ASC")
    suspend fun loadAllByIds(characterIds: IntArray): List<CharacterModel>

    @Query("SELECT * FROM charactermodel WHERE id = :characterId")
    suspend fun loadById(characterId: Int): CharacterModel

    @Insert
    suspend fun insertAll(vararg dbCharacter: CharacterModel)

    @Delete
    suspend fun delete(dbCharacter: CharacterModel)
}
