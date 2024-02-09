package com.coolnexttech.fireplayer.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.coolnexttech.fireplayer.appContext
import com.coolnexttech.fireplayer.model.SortOptions
import com.coolnexttech.fireplayer.model.Track
import com.coolnexttech.fireplayer.utils.extensions.filterByPlaylist
import com.coolnexttech.fireplayer.utils.extensions.sort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object FolderAnalyzer {

    private val unsupportedFileFormats = listOf("dsf")
    private const val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

    fun getTracksFromPlaylist(tracks: List<Track>, selectedPlaylistTitle: String): List<Track> {
        val playlists = UserStorage.readPlaylists()
        val selectedPlaylist = playlists[selectedPlaylistTitle]
        return if (selectedPlaylist != null) {
            tracks.filterByPlaylist(selectedPlaylist).sort(SortOptions.AToZ)
        } else {
            listOf()
        }
    }

    suspend fun getTracksFromMusicFolder(limit: Int? = null): ArrayList<Track> {
        return withContext(Dispatchers.IO) {
            var limitVal = 0
            val result = arrayListOf<Track>()

            val contentResolver = appContext.get()?.contentResolver
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.DATE_MODIFIED,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
            )
            val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

            contentResolver?.query(
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
                    val dateModifiedColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)

                    while (cursor.moveToNext() && limitVal != limit) {
                        val id = cursor.getLong(idColumn)
                        val title = cursor.getString(titleColumn).trimStart()
                        val artist = cursor.getString(artistColumn).trimStart()
                        val album = cursor.getString(albumColumn).trimStart()
                        val duration = cursor.getLong(durationColumn)
                        val dateModified = cursor.getLong(dateModifiedColumn)
                        val path =
                            ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                        val pathExtension = getFileMimeType(path)

                        if (!unsupportedFileFormats.contains(pathExtension)) {
                            val isPositionSaved = UserStorage.readTrackPlaybackPosition(id, false) != null

                            val track = Track(
                                id,
                                title,
                                artist,
                                album,
                                path,
                                duration,
                                pathExtension,
                                dateAdded = dateModified,
                                isPositionSaved
                            )

                            result.add(track)
                            limitVal += 1
                        }
                    }
                }
            }

            result
        }
    }

    fun deleteTrack(track: Track) {
        val resolver = appContext.get()?.contentResolver
        resolver?.delete(
            track.path,
            selection,
            null
        )
    }

    private fun getFileMimeType(uri: Uri): String? {
        return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(appContext.get()?.contentResolver?.getType(uri))
        } else {
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(uri.path?.let { File(it) }).toString())
        }
    }

}
