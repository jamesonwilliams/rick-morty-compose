package org.nosemaj.rickmorty.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DbCharacter(
    @PrimaryKey
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val image: String
)
