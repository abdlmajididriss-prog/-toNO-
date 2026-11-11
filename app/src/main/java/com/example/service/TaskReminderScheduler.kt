package com.example.service

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.local.entities.TaskEntity
import com.example.receiver.TaskReminderReceiver
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object TaskReminderScheduler {

    const val ACTION_TASK_REMINDER = "com.example.ACTION_TASK_REMINDER"
    const val CHANNEL_ID = "task_reminders_channel"
    const val EXTRA_TASK_ID = "EXTRA_TASK_ID"
    const val EXTRA_TASK_TITLE = "EXTRA_TASK_TITLE"
    const val EXTRA_TASK_CATEGORY = "EXTRA_TASK_CATEGORY"
    const val EXTRA_TASK_DESC = "EXTRA_TASK_DESC"

    fun scheduleReminder(context: Context, task: TaskEntity) {
        if (!task.isReminderEnabled || task.isCompleted || task.dueDate.isBlank()) {
            cancelReminder(context, task.id)
            return
        }

        val triggerMillis = calculateTriggerMillis(task.dueDate, task.dueTime, task.reminderMinutesBefore)
            ?: return

        // If the calculated reminder time has already passed, skip scheduling
        if (triggerMillis <= System.currentTimeMillis()) {
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val pendingIntent = createPendingIntent(context, task)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
            }
        } catch (e: SecurityException) {
            // Fallback for devices restricting exact alarms
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelReminder(context: Context, taskId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            action = ACTION_TASK_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }

        // Also dismiss any active notification with this ID
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.cancel(taskId.toInt())
    }

    fun sendImmediateTestNotification(
        context: Context,
        title: String = "تذكير تجريبي بمهمة",
        category: String = "عمل",
        description: String = "هذا تنبيه تجريبي للتأكد من وصول تذكيرات المهام في وقتها بنجاح!"
    ) {
        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            action = ACTION_TASK_REMINDER
            putExtra(EXTRA_TASK_ID, 99999L)
            putExtra(EXTRA_TASK_TITLE, title)
            putExtra(EXTRA_TASK_CATEGORY, category)
            putExtra(EXTRA_TASK_DESC, description)
        }
        context.sendBroadcast(intent)
    }

    fun calculateTriggerMillis(dueDate: String, dueTime: String, minutesBefore: Int): Long? {
        return try {
            val dateStr = dueDate.trim()
            val timeStr = if (dueTime.isNotBlank()) dueTime.trim() else "09:00"

            val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)
            val parsedDate = format.parse("$dateStr $timeStr") ?: return null

            val calendar = Calendar.getInstance().apply {
                time = parsedDate
                if (minutesBefore > 0) {
                    add(Calendar.MINUTE, -minutesBefore)
                }
            }
            calendar.timeInMillis
        } catch (e: Exception) {
            null
        }
    }

    private fun createPendingIntent(context: Context, task: TaskEntity): PendingIntent {
        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            action = ACTION_TASK_REMINDER
            putExtra(EXTRA_TASK_ID, task.id)
            putExtra(EXTRA_TASK_TITLE, task.title)
            putExtra(EXTRA_TASK_CATEGORY, task.category)
            putExtra(EXTRA_TASK_DESC, task.description)
        }
        return PendingIntent.getBroadcast(
            context,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
