package com.coolnexttech.fireplayer.utils

import androidx.activity.ComponentActivity
import com.coolnexttech.fireplayer.FirePlayer
import com.coolnexttech.fireplayer.model.Playlists
import com.coolnexttech.fireplayer.utils.extensions.jsonToPlaylists
import com.coolnexttech.fireplayer.utils.extensions.toJson

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
