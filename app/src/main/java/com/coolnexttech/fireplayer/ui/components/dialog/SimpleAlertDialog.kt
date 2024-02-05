package com.coolnexttech.fireplayer.ui.components.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.ui.theme.AppColors

@Composable
fun SimpleAlertDialog(titleId: Int, description: String,  content: @Composable (() -> Unit)? = null, onComplete: () -> Unit, dismiss: () -> Unit) {
    AlertDialog(
        containerColor = AppColors.alternateBackground,
        onDismissRequest = { dismiss() },
        title = {
            Text(text = stringResource(id = titleId))
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = description)

                content?.let {
                    Spacer(modifier = Modifier.height(16.dp))

                    content()
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onComplete()
                dismiss()
            }) {
                Text(
                    stringResource(id = R.string.common_ok),
                    color = AppColors.textColor
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { dismiss() }) {
                Text(
                    stringResource(id = R.string.common_cancel),
                    color = AppColors.textColor
                )
            }
        }
    )
}
