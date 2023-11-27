package com.coolnexttech.fireplayer.extensions

import com.coolnexttech.fireplayer.model.Playlists
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun String.jsonToPlaylists(): Playlists {
    val type = object : TypeToken<Playlists>() {}.type
    return Gson().fromJson<HashMap<String, ArrayList<String>>>(this, type) ?: return hashMapOf()
}

fun Playlists.toJson(): String = Gson().toJson(this)

fun Playlists.add(title: String) {
    if (containsKey(title)) {
        val updatedList = ArrayList(this[title]!!)
        updatedList.add(title)
        this[title] = updatedList
    } else {
        this[title] = arrayListOf(title)
    }
}