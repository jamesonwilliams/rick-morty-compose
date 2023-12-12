package org.nosemaj.rickmorty.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

// Yes, we're sharing a model across a few modules.
// Ends up significantly cutting down on maintenance in this small proj.
@JsonClass(generateAdapter = true)
@Entity
data class CharacterModel(
    @PrimaryKey
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val image: String
)
