package org.nosemaj.rickmorty.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [DbCharacter::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun characterDao(): DbCharacterDao
}
