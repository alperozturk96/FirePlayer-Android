package com.coolnexttech.fireplayer.util

import android.content.Context
import androidx.activity.ComponentActivity
import com.coolnexttech.fireplayer.extensions.jsonToPlaylists
import com.coolnexttech.fireplayer.extensions.toJson
import com.coolnexttech.fireplayer.model.Playlists

class UserStorage(
    private val context: Context
) {

    private val appPreferences = "FirePlayer"
    private val playlists = "playlists"

    private val sharedPreferences by lazy {
        context.getSharedPreferences(appPreferences, ComponentActivity.MODE_PRIVATE)
    }

    fun readPlaylists(): Playlists {
        return sharedPreferences.getString(playlists, null)?.jsonToPlaylists() ?: hashMapOf()
    }

    fun savePlaylists(value: Playlists) {
        sharedPreferences.edit().putString(playlists, value.toJson()).apply()
    }
}
