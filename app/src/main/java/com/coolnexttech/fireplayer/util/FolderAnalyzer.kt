package com.coolnexttech.fireplayer.util

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.coolnexttech.fireplayer.app.FirePlayer
import com.coolnexttech.fireplayer.extensions.filterByPlaylist
import com.coolnexttech.fireplayer.extensions.sort
import com.coolnexttech.fireplayer.model.SortOptions
import com.coolnexttech.fireplayer.model.Track
import java.io.File

object FolderAnalyzer {

    private val unsupportedFileFormats = listOf("dsf")

    var tracks: ArrayList<Track> = arrayListOf()

    init {
        fetchTrackList()
    }

    fun getTracksFromPlaylist(selectedPlaylistTitle: String): List<Track> {
        val playlists = UserStorage.readPlaylists()
        val selectedPlaylist = playlists[selectedPlaylistTitle]
        return if (selectedPlaylist != null) {
            tracks.filterByPlaylist(selectedPlaylist).sort(SortOptions.AtoZ)
        } else {
            listOf()
        }
    }

    fun fetchTrackList() {
        tracks = getTracksFromMusicFolder()
    }

    private fun getTracksFromMusicFolder(): ArrayList<Track> {
        val result = arrayListOf<Track>()

        val contentResolver = FirePlayer.context.contentResolver
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION
        )
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        ).use {
            it?.let { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn)
                    val artist = cursor.getString(artistColumn)
                    val album = cursor.getString(albumColumn)
                    val duration = cursor.getLong(durationColumn)
                    val path = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                    val pathExtension = getFileMimeType(path)

                    if (!unsupportedFileFormats.contains(pathExtension)) {
                        val track = Track(id, title, artist, album, path, duration, pathExtension)
                        result.add(track)
                    }
                }
            }
        }

        return result
    }

    private fun getFileMimeType(uri: Uri): String? {
        return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(FirePlayer.context.contentResolver.getType(uri))
        } else {
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(uri.path?.let { File(it) }).toString())
        }
    }

}
