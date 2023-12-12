package org.nosemaj.rickmorty.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import org.nosemaj.rickmorty.data.CharacterModel

@Database(
    entities = [CharacterModel::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
}
