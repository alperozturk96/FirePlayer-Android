package com.coolnexttech.fireplayer.utils.extensions

import android.content.ContentResolver
import android.net.Uri
import java.io.BufferedReader
import java.io.InputStreamReader

fun Uri?.toContentString(contentResolver: ContentResolver): String? {
    if (this == null) {
        return null
    }

    val stringBuilder = StringBuilder()
    contentResolver.openInputStream(this)?.use { inputStream ->
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line: String? = reader.readLine()
        while (line != null) {
            stringBuilder.append(line)
            line = reader.readLine()
        }
    }
    return stringBuilder.toString()
}
