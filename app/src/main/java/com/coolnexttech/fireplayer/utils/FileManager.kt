package com.coolnexttech.fireplayer.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.coolnexttech.fireplayer.R
import java.io.OutputStream

object FileManager {
    fun writeToDocuments(context: Context, fileName: String, content: String): Boolean {
        val appName = context.getString(R.string.app_name)

        val values = ContentValues()

        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
        values.put(
            MediaStore.MediaColumns.RELATIVE_PATH,
            Environment.DIRECTORY_DOCUMENTS + "/${appName}/"
        )

        val uri: Uri =
            context.contentResolver.insert(MediaStore.Files.getContentUri("external"), values)
                ?: return false

        val outputStream: OutputStream? = context.contentResolver.openOutputStream(uri)

        outputStream?.bufferedWriter().use { writer ->
            writer?.write(content)
        }
        outputStream?.close()
        return true
    }
}