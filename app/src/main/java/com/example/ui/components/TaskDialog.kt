package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.local.entities.TaskEntity
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.ui.text.font.FontWeight
import com.example.ui.theme.LowPriorityColor
import com.example.ui.theme.MediumPriorityColor
import com.example.ui.theme.UrgentPriorityColor
import com.example.ui.util.ArabicDateUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskDialog(
    task: TaskEntity,
    onDismiss: () -> Unit,
    onSave: (TaskEntity) -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var priority by remember { mutableStateOf(task.priority) }
    var category by remember { mutableStateOf(task.category) }
    var dueDate by remember {
        mutableStateOf(if (task.dueDate.isEmpty()) ArabicDateUtils.getTodayDateString() else task.dueDate)
    }
    var dueTime by remember { mutableStateOf(task.dueTime) }
    var isReminderEnabled by remember { mutableStateOf(task.isReminderEnabled) }
    var reminderMinutesBefore by remember { mutableStateOf(task.reminderMinutesBefore) }
    var isTitleError by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    val categories = listOf("عمل", "شخصي", "دراسة", "صحة", "تسوق", "عام")

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("task_dialog"),
        title = {
            Text(
                text = if (task.id == 0L) "إضافة مهمة جديدة" else "تعديل المهمة",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Task Title
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) isTitleError = false
                    },
                    label = { Text("عنوان المهمة *") },
                    placeholder = { Text("مثال: تسليم المشروع أو الاتصال بالعميل") },
                    isError = isTitleError,
                    supportingText = if (isTitleError) {
                        { Text("يرجى كتابة عنوان المهمة") }
                    } else null,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_title_input")
                )

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("التفاصيل والملاحظات (اختياري)") },
                    placeholder = { Text("أضف أي تفاصيل أو خطوات فرعية...") },
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_desc_input")
                )

                // Priority Selection
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "مستوى الأولوية:", style = MaterialTheme.typography.labelLarge)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // High
                        FilterChip(
                            selected = priority == "HIGH",
                            onClick = { priority = "HIGH" },
                            label = { Text("عالية") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = UrgentPriorityColor.copy(alpha = 0.2f),
                                selectedLabelColor = UrgentPriorityColor
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        // Medium
                        FilterChip(
                            selected = priority == "MEDIUM",
                            onClick = { priority = "MEDIUM" },
                            label = { Text("متوسطة") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MediumPriorityColor.copy(alpha = 0.2f),
                                selectedLabelColor = MediumPriorityColor
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        // Low
                        FilterChip(
                            selected = priority == "LOW",
                            onClick = { priority = "LOW" },
                            label = { Text("منخفضة") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LowPriorityColor.copy(alpha = 0.2f),
                                selectedLabelColor = LowPriorityColor
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Category Selection
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Label,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "التصنيف:", style = MaterialTheme.typography.labelLarge)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        com.example.ui.util.TaskCategoryUtils.predefinedCategories.forEach { catInfo ->
                            val isSelected = category == catInfo.name
                            FilterChip(
                                selected = isSelected,
                                onClick = { category = catInfo.name },
                                label = { Text(catInfo.name) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = catInfo.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp),
                                        tint = if (isSelected) catInfo.primaryColor else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = catInfo.containerColorLight,
                                    selectedLabelColor = catInfo.onContainerColorLight
                                )
                            )
                        }
                    }
                }

                // Date and Time Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        label = { Text("التاريخ") },
                        placeholder = { Text("2026-11-11") },
                        leadingIcon = {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp))
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = dueTime,
                        onValueChange = { dueTime = it },
                        label = { Text("الوقت") },
                        placeholder = { Text("14:30") },
                        leadingIcon = {
                            Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(18.dp))
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Reminder Notification Switch and Options
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isReminderEnabled) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = if (isReminderEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "تفعيل التذكير والتنبيه 🔔",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "إرسال إشعار تنبيهي على الجهاز في الموعد",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Switch(
                                checked = isReminderEnabled,
                                onCheckedChange = { enabled ->
                                    isReminderEnabled = enabled
                                    if (enabled && Build.VERSION.SDK_INT >= 33) {
                                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        }
                                    }
                                },
                                modifier = Modifier.testTag("task_reminder_switch")
                            )
                        }

                        if (isReminderEnabled) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "توقيت إرسال التنبيه:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    0 to "في الموعد",
                                    15 to "قبل 15 د",
                                    30 to "قبل 30 د",
                                    60 to "قبل 1 س"
                                ).forEach { (minutes, label) ->
                                    val isSelected = reminderMinutesBefore == minutes
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { reminderMinutesBefore = minutes },
                                        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        isTitleError = true
                    } else {
                        onSave(
                            task.copy(
                                title = title.trim(),
                                description = description.trim(),
                                priority = priority,
                                category = category,
                                dueDate = dueDate.trim(),
                                dueTime = dueTime.trim(),
                                isReminderEnabled = isReminderEnabled,
                                reminderMinutesBefore = reminderMinutesBefore
                            )
                        )
                    }
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("save_task_button")
            ) {
                Text("حفظ المهمة")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("إلغاء")
            }
        }
    )
}
