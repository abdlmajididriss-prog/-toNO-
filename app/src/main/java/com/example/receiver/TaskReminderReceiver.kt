package com.example.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.AppDatabase
import com.example.service.TaskReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class TaskReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        if (action == Intent.ACTION_BOOT_COMPLETED) {
            // Reschedule active reminders on device reboot
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getInstance(context)
                    val tasks = db.taskDao().getAllTasks().firstOrNull() ?: emptyList()
                    val activeReminders = tasks.filter { !it.isCompleted && it.isReminderEnabled }
                    activeReminders.forEach { task ->
                        TaskReminderScheduler.scheduleReminder(context, task)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return
        }

        if (action == TaskReminderScheduler.ACTION_TASK_REMINDER) {
            val taskId = intent.getLongExtra(TaskReminderScheduler.EXTRA_TASK_ID, 0L)
            val taskTitle = intent.getStringExtra(TaskReminderScheduler.EXTRA_TASK_TITLE) ?: "مهمة مجدولة"
            val taskCategory = intent.getStringExtra(TaskReminderScheduler.EXTRA_TASK_CATEGORY) ?: "عام"
            val taskDesc = intent.getStringExtra(TaskReminderScheduler.EXTRA_TASK_DESC) ?: ""

            showNotification(context, taskId, taskTitle, taskCategory, taskDesc)
        }
    }

    private fun showNotification(
        context: Context,
        taskId: Long,
        title: String,
        category: String,
        description: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = TaskReminderScheduler.CHANNEL_ID

        // Create Channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "تذكيرات المهام المجدولة"
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                this.description = "قناة إرسال التنبيهات والتذكيرات للمهام المجدولة في وقتها المحدد"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Tap action opens MainActivity
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_NAV_TAB", "TASKS")
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId.toInt(),
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBody = when {
            description.isNotBlank() -> "[$category] $description"
            else -> "حان موعد تنفيذ المهمة المجدولة: $category"
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_app_logo)
            .setContentTitle("تذكير بمهمة: $title")
            .setContentText(notificationBody)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationBody))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(taskId.toInt(), notification)
    }
}
