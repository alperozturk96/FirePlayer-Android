package com.coolnexttech.fireplayer.utils

import android.content.ContextWrapper
import android.os.Environment
import androidx.activity.ComponentActivity
import com.coolnexttech.fireplayer.FirePlayer
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.model.Playlists
import com.coolnexttech.fireplayer.utils.extensions.jsonToPlaylists
import com.coolnexttech.fireplayer.utils.extensions.showToast
import com.coolnexttech.fireplayer.utils.extensions.toJson
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.OutputStreamWriter

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

    fun exportPlaylists() {
        val playlists = readPlaylists()
        val playlistsAsJson = playlists.toJson()

        val file = getPlaylistFile()
        val fos: FileOutputStream?
        try {
            fos = FileOutputStream(file)
            val osw = OutputStreamWriter(fos)
            osw.write(playlistsAsJson)
            osw.flush()
            osw.close()
            fos.close()

            val successMessage = FirePlayer.context.getString(
                R.string.user_storage_export_success_message,
                file.path.substring(20)
            )
            FirePlayer.context.showToast(successMessage)
        } catch (e: Exception) {
            FirePlayer.context.showToast(R.string.user_storage_export_fail_message)
        }
    }

    fun importPlaylists() {
        val playlistFile = getPlaylistFile()
        val stringBuilder = StringBuilder()
        try {
            val br = BufferedReader(FileReader(playlistFile))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                stringBuilder.append(line)
                stringBuilder.append("")
            }
            br.close()
        } catch (e: Exception) {
            FirePlayer.context.showToast(R.string.user_storage_import_fail_message)
        }

        FirePlayer.context.showToast(R.string.user_storage_import_success_message)

        val playlistsAsJson = stringBuilder.toString()
        val playlist = playlistsAsJson.jsonToPlaylists()
        savePlaylists(playlist)
    }

    private fun getPlaylistFile(): File {
        val contextWrapper = ContextWrapper(FirePlayer.context)
        val directory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        return File(directory, "FirePlayerPlaylists" + ".txt")
    }
}
