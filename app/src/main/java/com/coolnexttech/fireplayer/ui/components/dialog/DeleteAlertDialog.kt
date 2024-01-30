package com.coolnexttech.fireplayer.ui.components.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.ui.theme.AppColors

@Composable
fun DeleteAlertDialog(onComplete: () -> Unit, dismiss: () -> Unit) {
    AlertDialog(
        containerColor = AppColors.alternateBackground,
        onDismissRequest = { dismiss() },
        title = {
            Text(text = stringResource(id = R.string.delete_alert_dialog_title))
        },
        text = {
            Text(text = stringResource(id = R.string.delete_alert_dialog_description))
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
