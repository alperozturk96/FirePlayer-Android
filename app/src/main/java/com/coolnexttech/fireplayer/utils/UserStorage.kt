package com.coolnexttech.fireplayer.utils

import android.content.ContextWrapper
import android.os.Environment
import androidx.activity.ComponentActivity
import com.coolnexttech.fireplayer.appContext
import com.coolnexttech.fireplayer.model.Playlists
import com.coolnexttech.fireplayer.utils.extensions.jsonToPlaylists
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
        appContext.get()?.getSharedPreferences(appPreferences, ComponentActivity.MODE_PRIVATE)
    }

    fun readPlaylists(): Playlists {
        return sharedPreferences?.getString(playlists, null)?.jsonToPlaylists() ?: hashMapOf()
    }

    fun savePlaylists(value: Playlists) {
        sharedPreferences?.edit()?.putString(playlists, value.toJson())?.apply()
    }

    fun removeTrackPlaybackPosition(id: Long) {
        sharedPreferences?.edit()?.remove(id.toString())?.apply()
        ToastManager.showRemoveTrackPlaybackPosition()
    }

    fun saveTrackPlaybackPosition(id: Long?, position: Long?) {
        if (id == null || position == null) return
        sharedPreferences?.edit()?.putLong(id.toString(), position)?.apply()
        ToastManager.showSaveTrackPlaybackPositionMessage()
    }

    fun readTrackPlaybackPosition(id: Long, showToast: Boolean): Long? {
        val result = sharedPreferences?.getLong(id.toString(), 0L)
        return if (result == 0L) {
            null
        } else {
            if (showToast && result != null) {
                ToastManager.showReadTrackPlaybackPositionMessage(result)
            }

            result
        }
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

            ToastManager.showSuccessExportPlaylistMessage(file.path.substring(20))
        } catch (e: Exception) {
            ToastManager.showFailExportPlaylistMessage()
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
            ToastManager.showFailImportPlaylistMessage()
        }

        ToastManager.showSuccessImportPlaylistMessage()

        val playlistsAsJson = stringBuilder.toString()
        val playlist = playlistsAsJson.jsonToPlaylists()
        savePlaylists(playlist)
    }

    private fun getPlaylistFile(): File {
        val contextWrapper = ContextWrapper(appContext.get())
        val directory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        return File(directory, "FirePlayerPlaylists" + ".txt")
    }
}
