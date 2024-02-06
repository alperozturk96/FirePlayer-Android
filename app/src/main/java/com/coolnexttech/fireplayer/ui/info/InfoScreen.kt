package com.coolnexttech.fireplayer.ui.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.ui.theme.AppColors

@Composable
fun InfoScreen() {
    val data: List<Triple<Int, Int, Int>> = listOf(
        Triple(
            R.drawable.ic_reset,
            R.string.info_screen_reset_button_title,
            R.string.info_screen_reset_button_description
        ),
        Triple(
            R.drawable.ic_playlists,
            R.string.info_screen_playlists_button_title,
            R.string.info_screen_playlists_button_description
        ),
        Triple(
            R.drawable.ic_sleep_timer,
            R.string.info_screen_sleep_timer_button_title,
            R.string.info_screen_sleep_timer_button_description
        ),
        Triple(
            R.drawable.ic_sort,
            R.string.info_screen_sort_button_title,
            R.string.info_screen_sort_button_description
        ),
        Triple(
            R.drawable.ic_title,
            R.string.info_screen_filter_mode_button_title,
            R.string.info_screen_filter_mode_button_description
        ),
        Triple(
            R.drawable.ic_save,
            R.string.info_screen_save_track_position_button_title,
            R.string.info_screen_save_track_position_button_description
        ),
        Triple(
            R.drawable.ic_reset,
            R.string.info_screen_reset_track_position_button_title,
            R.string.info_screen_reset_track_position_button_description
        ),
        Triple(
            R.drawable.ic_import,
            R.string.info_screen_import_playlist_button_title,
            R.string.info_screen_import_playlist_button_description
        ),
        Triple(
            R.drawable.ic_export,
            R.string.info_screen_export_playlist_button_title,
            R.string.info_screen_export_playlist_button_description
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp)
    ) {
        items(data) { (iconId, titleId, descriptionId) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        shape = RoundedCornerShape(16.dp),
                        color = AppColors.alternateBackground
                    )
                    .padding(all = 16.dp)
            ) {
                Row {
                    Image(painter = painterResource(id = iconId), contentDescription = "Icon")

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = stringResource(id = titleId),
                            fontSize = 18.sp,
                            color = AppColors.textColor
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(id = descriptionId),
                            fontSize = 12.sp,
                            color = AppColors.textColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
