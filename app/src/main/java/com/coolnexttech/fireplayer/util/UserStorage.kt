package com.coolnexttech.fireplayer.util

import androidx.activity.ComponentActivity
import com.coolnexttech.fireplayer.app.FirePlayer
import com.coolnexttech.fireplayer.extensions.jsonToPlaylists
import com.coolnexttech.fireplayer.extensions.toJson
import com.coolnexttech.fireplayer.model.Playlists

object UserStorage {

    private const val appPreferences = "FirePlayer"
    private const val playlists = "playlists"

    private val sharedPreferences by lazy {
        FirePlayer.context.getSharedPreferences(appPreferences, ComponentActivity.MODE_PRIVATE)
    }

    fun readPlaylists(): Playlists {
        return sharedPreferences.getString(playlists, null)?.jsonToPlaylists() ?: hashMapOf()
    }

    fun savePlaylists(value: Playlists) {
        sharedPreferences.edit().putString(playlists, value.toJson()).apply()
    }
}
