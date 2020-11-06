package com.github.sample.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserAlbum(
    val albumId: Int,
    val id: Int,
    val title: String,
    val url: String,
    val thumbnailUrl: String
)