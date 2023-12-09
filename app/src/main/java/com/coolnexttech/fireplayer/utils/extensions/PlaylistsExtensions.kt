package com.coolnexttech.fireplayer.utils.extensions

import com.coolnexttech.fireplayer.model.Playlists
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun String.jsonToPlaylists(): Playlists {
    val type = object : TypeToken<Playlists>() {}.type
    return Gson().fromJson<HashMap<String, ArrayList<Long>>>(this, type) ?: return hashMapOf()
}

fun Playlists.toJson(): String = Gson().toJson(this)

