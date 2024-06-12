package com.coolnexttech.fireplayer.utils

import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.coolnexttech.fireplayer.appContext
import com.coolnexttech.fireplayer.db.PlaylistBox
import com.coolnexttech.fireplayer.db.TrackBox
import com.coolnexttech.fireplayer.db.TrackEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Collections


object FolderAnalyzer {

    private val unsupportedFileFormats = listOf("dsf")
    private const val SELECTION = MediaStore.Audio.Media.IS_MUSIC + " != 0"

    fun getTracksFromPlaylist(id: Long): List<TrackEntity> {
        return PlaylistBox.get(id).tracks
    }

    private suspend fun getTracksFromMusicFolder(limit: Int? = null): List<TrackEntity> {
        return withContext(Dispatchers.IO) {
            var limitVal = 0
            val result = arrayListOf<TrackEntity>()

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
                SELECTION,
                null,
                sortOrder
            ).use {
                it?.let { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                    val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                    val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                    val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                    val durationColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
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
                            ContentUris.withAppendedId(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                id
                            )
                        val pathExtension = getFileMimeType(path)

                        if (!unsupportedFileFormats.contains(pathExtension)) {
                            val savedPosition =
                                UserStorage.readTrackPlaybackPosition(id, false)

                            val track = TrackEntity(
                                id,
                                title,
                                artist,
                                album,
                                path.toString(),
                                duration,
                                pathExtension,
                                dateAdded = dateModified,
                                savedPosition
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

    suspend fun getTracks(limit: Int? = null): List<TrackEntity> {
        return TrackBox.getAll().ifEmpty {
            addScannedTracksToTracksEntity(limit)
        }
    }

    suspend fun addScannedTracksToTracksEntity(limit: Int?): List<TrackEntity> {
        val scannedTracks = getTracksFromMusicFolder(null)
        TrackBox.addAll(scannedTracks)
        return scannedTracks
    }

    fun deleteTrack(
        track: TrackEntity,
        contentResolver: ContentResolver,
        launcher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val result = ArrayList<Uri>().apply {
                add(track.getUri())
            }

            Collections.addAll(result)

            val intentSender = MediaStore.createDeleteRequest(contentResolver, result).intentSender
            val senderRequest = IntentSenderRequest.Builder(intentSender)
                .setFillInIntent(null)
                .setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION, 0)
                .build()

            launcher.launch(senderRequest)
            TrackBox.remove(track.id)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                contentResolver.delete(track.getUri(), null, null)
                TrackBox.remove(track.id)
            } catch (securityException: SecurityException) {
                Log.d("FolderAnalyzer", "Error caught at deleteTrack: $securityException")
                val recoverableSecurityException = securityException as RecoverableSecurityException
                val senderRequest = IntentSenderRequest.Builder(recoverableSecurityException.userAction.actionIntent.intentSender).build()
                launcher.launch(senderRequest)
            }
        } else {
            try {
                contentResolver.delete(
                    track.getUri(),
                    SELECTION,
                    null
                )
                TrackBox.remove(track.id)
            } catch (e: Exception) {
                Log.d("FolderAnalyzer", "Error caught at deleteTrack: $e")
            }
        }
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
