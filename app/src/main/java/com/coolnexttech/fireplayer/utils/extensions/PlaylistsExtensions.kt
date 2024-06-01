package com.coolnexttech.fireplayer.utils.extensions

import com.coolnexttech.fireplayer.db.PlaylistEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun String.jsonToPlaylists(): List<PlaylistEntity> {
    val type = object : TypeToken<List<PlaylistEntity>>() {}.type
    return Gson().fromJson<List<PlaylistEntity>>(this, type) ?: return listOf()
}

fun List<PlaylistEntity>.toJson(): String = Gson().toJson(this)

