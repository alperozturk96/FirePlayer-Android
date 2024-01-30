package com.coolnexttech.fireplayer.ui.playlists.topbar

import android.content.ContentResolver
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.ui.components.ActionIconButton
import com.coolnexttech.fireplayer.ui.components.HeadlineMediumText
import com.coolnexttech.fireplayer.ui.playlists.PlaylistsViewModel
import com.coolnexttech.fireplayer.utils.UserStorage
import com.coolnexttech.fireplayer.utils.extensions.getTopAppBarColor
import java.io.BufferedReader
import java.io.InputStreamReader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsTopBar(viewModel: PlaylistsViewModel, showAddPlaylist: () -> Unit) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            val playlistAsJson = readUri(contentResolver, it)
            UserStorage.importPlaylist(playlistAsJson)
            viewModel.readPlaylists()
        }
    )

    TopAppBar(
        colors = getTopAppBarColor(),
        title = {
            HeadlineMediumText(text = stringResource(id = R.string.playlists_title))
        },
        actions = {
            ActionIconButton(R.drawable.ic_add_playlist) {
                showAddPlaylist()
            }

            ActionIconButton(R.drawable.ic_import) {
                importLauncher.launch("text/plain")
            }

            ActionIconButton(R.drawable.ic_export) {
                UserStorage.exportPlaylists(context)
            }
        }
    )
}

private fun readUri(contentResolver: ContentResolver, uri: Uri?): String? {
    if (uri == null) {
        return null
    }

    val stringBuilder = StringBuilder()
    contentResolver.openInputStream(uri)?.use { inputStream ->
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line: String? = reader.readLine()
        while (line != null) {
            stringBuilder.append(line)
            line = reader.readLine()
        }
    }
    return stringBuilder.toString()
}
