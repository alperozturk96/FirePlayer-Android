package com.coolnexttech.fireplayer.util

import android.content.Context
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.coolnexttech.fireplayer.model.Track

class FolderAnalyzer(private val context: Context) {

    fun getTracksFromFolder(folderPath: String): List<Track> {
        val result = mutableListOf<Track>()

        val contentResolver = context.contentResolver

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA // path to file
        )

        val selection = "${MediaStore.Audio.Media.DATA} LIKE ?"
        val selectionArgs = arrayOf("%$folderPath%")

        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

        val queryUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        contentResolver.query(queryUri, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val album = cursor.getString(albumColumn)
                val path = cursor.getString(pathColumn)
                val mimeType = cursor.getString(mimeTypeColumn)
                val pathExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)

                val track = Track(id, title, artist, album, path, pathExtension)
                result.add(track)
            }
        }

        return result
    }
}
