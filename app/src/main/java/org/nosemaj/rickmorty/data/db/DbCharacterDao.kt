package org.nosemaj.rickmorty.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DbCharacterDao {
    @Query("SELECT * FROM dbcharacter WHERE id IN (:characterIds) ORDER BY id ASC")
    suspend fun loadAllByIds(characterIds: IntArray): List<DbCharacter>

    @Query("SELECT * FROM dbcharacter WHERE id = :characterId")
    suspend fun loadById(characterId: Int): DbCharacter

    @Insert
    suspend fun insertAll(vararg dbCharacter: DbCharacter)

    @Delete
    suspend fun delete(dbCharacter: DbCharacter)
}
