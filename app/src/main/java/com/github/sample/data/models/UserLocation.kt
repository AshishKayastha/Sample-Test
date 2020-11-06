package com.github.sample.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserLocation(val lat: String, val lng: String)