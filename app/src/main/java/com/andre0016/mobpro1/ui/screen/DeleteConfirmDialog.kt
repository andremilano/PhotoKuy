package com.andre0016.mobpro1.ui.screen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.andre0016.mobpro1.R
import com.andre0016.mobpro1.model.GalleryItem

@Composable
fun DeleteConfirmDialog(
    item: GalleryItem,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.hapus_item))
        },
        text = {
            Text(text = stringResource(R.string.konfirmasi, item.title))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.hapus))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.batal))
            }
        }
    )
}
