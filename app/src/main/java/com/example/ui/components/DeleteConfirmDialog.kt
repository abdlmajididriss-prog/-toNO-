package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.viewmodel.DeleteTarget

@Composable
fun DeleteConfirmDialog(
    target: DeleteTarget,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val (itemType, itemTitle) = when (target) {
        is DeleteTarget.Task -> "المهمة" to target.task.title
        is DeleteTarget.Note -> "الملاحظة" to target.note.title
        is DeleteTarget.Transaction -> "المعاملة" to "${target.transaction.title} (${target.transaction.amount})"
        is DeleteTarget.Event -> "الموعد" to target.event.title
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("delete_confirm_dialog"),
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(text = "تأكيد الحذف", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Text(
                text = "هل أنت متأكد من رغبتك في حذف $itemType \"$itemTitle\" نهائياً؟",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.testTag("confirm_delete_button")
            ) {
                Text("نعم، احذف")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
