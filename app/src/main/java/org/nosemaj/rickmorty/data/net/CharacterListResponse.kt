package org.nosemaj.rickmorty.data.net

import com.squareup.moshi.JsonClass
import org.nosemaj.rickmorty.data.CharacterModel

@JsonClass(generateAdapter = true)
data class CharacterListResponse(
    val info: Info,
    val results: List<CharacterModel>
) {
    @JsonClass(generateAdapter = true)
    data class Info(
        val count: Int,
        val next: String?,
        val pages: Int,
        val prev: String?
    )
}
